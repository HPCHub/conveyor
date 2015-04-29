import cx_Oracle
import os
import sys
import re
import shutil
import gzip


#
# Generate new unique tag name for read set (actually no :)
#
def getUniqueTagName(cursor, sampleName):

    row = cursor.execute(
        "SELECT * FROM (SELECT tag FROM read_sets WHERE tag LIKE :tag ORDER BY tag DESC) WHERE rownum = 1",
        { ":tag" : sampleName + "\_%" }
    ).fetchone()

    if cursor.rowcount == 0:
        return sampleName + "_1"

    matcher = re.search("\d+$", row[0])
    number  = matcher.group()

    return sampleName + "_" + str(int(number) + 1)


intputReferenceIndex  = "${input.reference.index}"
inputCsfastaFile      = "${input.csfasta.origin}"
inputBpCoverageFile   = "${input.bp-coverage.file}"
inputPosCoverageFile  = "${input.pos-coverage.file}"
projectOutputDir      = "${project.output.dir}"
projectLogDir         = "${project.log.dir}"
logFile               = open(projectLogDir + os.sep + "db.log", "wb")
sampleName            = "${sample.name}"
runName               = "${run.name}"

hmpReferences = (122, 182, 202)

#def this.isArchiveFile( filename ): 
#	return /.*?\.gz|bz2|rar|zip$/.test(filename);




nreads  = 0
my_file_size = 0
pattern = re.compile("^\w\d+")
if(os.path.isfile(inputCsfastaFile)): 
    fn = inputCsfastaFile
    my_file_size = os.path.getsize(inputCsfastaFile)
    # TODO - make it properly
    if fn.endswith(".gz"):    
        fn = gzip.GzipFile(fn, "rb")
        for line in iter(fn):
            if pattern.match(line) is not None:
                nreads += 1
        fn.close()  
    else:     
        csfastaFileDecriptor = open(inputCsfastaFile, "rb")
        for line in iter(csfastaFileDecriptor):
            if pattern.match(line) is not None:
                nreads += 1
        csfastaFileDecriptor.close()
else:         
    print "ERROR: Cannot open read set file - setting nreads = 0 and set_size = 0"        
    

if intputReferenceIndex.lower().find("bgi") > 0:
    refGroupId = 21
elif intputReferenceIndex.lower().find("hmp") > 0:
    refGroupId = int("${ref.group.id}");
else:
    print "ERROR: Cannot recognize reference type. Available values are [HMP, BGI]"
    sys.exit()

logFile.write("refGroupId\t" + str(refGroupId) + "\n")


#
# Get sample name by reference file path from msdba, if necessary
#
if sampleName.startswith("$") & runName.startswith("$"):
    connection = cx_Oracle.connect("msdba/msdba@10.210.31.7:1521/biodb")
    cursor = connection.cursor()

    cursor.execute(
        "SELECT seqs$samples.name, seqs$runs.name \
         FROM seqs$files \
         JOIN seqs$read_sets USING(file_id) \
         JOIN seqs$samples USING(sample_id) \
         JOIN seqs$runs_files USING(file_id) \
         JOIN seqs$runs USING(run_id) \
         WHERE path = :path",
         { ":path" : inputCsfastaFile }
    )
    
    row = cursor.fetchone()
    if row != None:
        sampleName = row[0]
        runName    = row[1]
    else:
        print "ERROR: Cannot recognize sample name by read set file"
        sys.exit()

    cursor.close()
    connection.close()


#
# Check for sampleName and runName properly initialization
#
if sampleName.startswith("$") & runName.startswith("$"):
    print 'ERROR: Cannot init sampleName and runName. Values %r and %r' % (sampleName, runName)
    sys.exit() 

logFile.write("seqsSampleName\t" + sampleName + "\n")
logFile.write("seqsRunName\t" + runName + "\n")

#
# Connect to solid db to deal with it.
#
connection = cx_Oracle.connect("solid/solid@10.210.31.7:1521/biodb")
cursor = connection.cursor()


#
# Reserve variable for SQL-queries return value.
#
returnValue = cursor.var(cx_Oracle.NUMBER)


#
# Add record to SOLID_RUNS table and get new row primary key
#
cursor.execute("SELECT solid_run_id FROM solid_runs WHERE name = :name", {":name" : runName})
row = cursor.fetchone()
if row != None:
    solidRunId = int(row[0])
else:
    cursor.execute(
        "INSERT INTO solid_runs(run_date, name) \
         VALUES(SYSDATE, :name) \
         RETURNING solid_run_id INTO :solid_run_id",
        {":name" : runName, ":solid_run_id" : returnValue}
    )
    solidRunId = int(returnValue.getvalue())
logFile.write("solidRunId\t" + str(solidRunId) + "\n")


#
# Add record to MAPPING_RUNS table and get new row primary key
#
cursor.execute(
    "INSERT INTO mapping_runs (mapping_program, version, paramstring, ref_group_id) \
     VALUES('bowtie', '0.12.8', 'jsub-bowtie-module', :ref_group_id) \
     RETURNING mapping_run_id INTO :mapping_run_id",
    {":ref_group_id" : refGroupId, "mapping_run_id" : returnValue}
)
mappingRunId = int(returnValue.getvalue())
logFile.write("mappingRunId\t" + str(mappingRunId) + "\n")


#
# Get sample_id value
#
cursor.execute("SELECT sample_id FROM samples WHERE name = :name", {":name" : sampleName})
row = cursor.fetchone()
if row != None:
    sampleId = int(row[0])
else:
    cursor.execute(
        "INSERT INTO samples (name) VALUES (:name) \
         RETURNING sample_id INTO :sample_id",
        {":name" : sampleName, ":sample_id" : returnValue}
    )
    sampleId = int(returnValue.getvalue())
logFile.write("sampleId\t" + str(sampleId) + "\n")



#
# Add record to READ_SETS table and get new row primary key
#
cursor.execute(
    "INSERT INTO read_sets (location, nreads, set_size, solid_run_id, sample_id, tag) \
     VALUES(:location, :nreads, :set_size, :solid_run_id, :sample_id, :tag) \
     RETURNING read_set_id INTO :read_set_id",
    {
        ":location" : inputCsfastaFile,
        ":nreads" : nreads,
        ":set_size" : my_file_size, #os.path.getsize(csfastaFileDecriptor.name),
        ":solid_run_id" : solidRunId,
        ":sample_id" : sampleId,
        ":tag" : getUniqueTagName(cursor, sampleName),
        ":read_set_id" : returnValue
    }
)
readSetId = int(returnValue.getvalue())
logFile.write("readSetId\t" + str(readSetId) + "\n")


#
# Be sure to close database connection before invoke sql loader sqlldr.
#
connection.commit()
cursor.close()
connection.close()


#
# Create files for loading to db.
# See http://docs.oracle.com/cd/B28359_01/server.111/b28319/ldr_concepts.htm#autoId6 for details
#
bpFilename = projectOutputDir + os.sep + 'load' + str(mappingRunId) + '_' + str(readSetId) + '_b.ctl'
bpSqlldrLogFilename =  os.path.splitext(bpFilename)[0] + ".log"
bpFile   = open(bpFilename, "a+")
bpFile.write(
    'LOAD DATA INFILE \'' + inputBpCoverageFile + '\' \"str \'\\n\'\"\n' +
    'APPEND INTO TABLE REL_COVERAGE\n' +
    'FIELDS TERMINATED BY \'\\t\'\n' + 
    'TRAILING NULLCOLS (\n' + 
    '\tmapping_run_id CONSTANT "' + str(mappingRunId) + '",\n' +
    '\tread_set_id CONSTANT "' + str(readSetId) + '",\n' +
    '\tref_name,\n' + 
    '\tbp_coverage,\n' + 
    '\tfasta_item_id,\n' + 
    '\trel_coverage_id "rel_coverage_seq.nextval"\n)'
)
bpFile.close() 


posFilename = projectOutputDir + os.sep + 'load' + str(mappingRunId) + '_' + str(readSetId) + '_p.ctl'
posSqlldrLogFilename =  os.path.splitext(posFilename)[0] + ".log"
posFile  = open(posFilename, "a+")
posFile.write(
    'LOAD DATA INFILE \'' + inputPosCoverageFile + '\' \"str \'\\n\'\"\n' +
    'APPEND INTO TABLE POS_COVERAGE\n' +
    'FIELDS TERMINATED BY \'\\t\'\n' + 
    'TRAILING NULLCOLS (\n' + 
    '\tmapping_run_id CONSTANT "' + str(mappingRunId) + '",\n' +
    '\tread_set_id CONSTANT "' + str(readSetId) + '",\n' +
    '\tref_name,\n' + 
    '\tpos_coverage,\n' + 
    '\tfasta_item_id,\n' + 
    '\tpos_coverage_id "pos_coverage_id_seq.nextval"\n)'
)
posFile.close() 


#
# Load files to db.
#
command = "${sqlldr.path} solid/solid@10.210.31.7:1521/biodb control=" + bpFilename + " log=" + bpSqlldrLogFilename
os.system(command)
command = "${sqlldr.path} solid/solid@10.210.31.7:1521/biodb control=" + posFilename + " log=" + posSqlldrLogFilename
os.system(command)


#
# Connect to database again.
#
connection = cx_Oracle.connect("solid/solid@10.210.31.7:1521/biodb")
cursor = connection.cursor()


#
# Call finalize oracle procedures for HMP for taxonomic (HMP).
#
if refGroupId in hmpReferences:
    cursor.callproc("insert_genome_coverage", [ mappingRunId ])
    cursor.callproc("insert_pos_genome_coverage", [ mappingRunId ])
    cursor.callproc("insert_mapping_read_sets", [ mappingRunId, refGroupId ])


#
# Now call procedures to generate result files and downloads them to build driectory.
# For taxonomic (HMP)
#
if refGroupId in hmpReferences:
    firstOutputFileName  = runName + "_" + str(mappingRunId) + ".genus.txt"
    secondOutputFileName = runName + "_" + str(mappingRunId) + ".org.txt"
    
    cursor.callproc("create_orgpos_vectors", [ mappingRunId, refGroupId, firstOutputFileName ])
    cursor.callproc("create_genus_vectors", [ mappingRunId, refGroupId, 1, firstOutputFileName ])
    cursor.callproc("create_org_vectors", [ mappingRunId, refGroupId, 1, secondOutputFileName ])
# For functional (BGI)
else:
    firstOutputFileName  = runName + "_" + str(mappingRunId) + ".GO.txt"
    secondOutputFileName = runName + "_" + str(mappingRunId) + ".COG.txt"
    thirdOutputFileName = runName + "_" + str(mappingRunId) + ".KObp.txt"

    cursor.callproc("create_go_vectors", [ mappingRunId, firstOutputFileName ])
    cursor.callproc("create_cog_vectors", [ mappingRunId, secondOutputFileName ])
    cursor.callproc("create_ko_vectors", [ mappingRunId, thirdOutputFileName ])#!!!!!


connection.commit()
cursor.close()
connection.close()

if refGroupId in hmpReferences:
    try:
        shutil.move("/data_fatso/xml_output/" + firstOutputFileName, projectOutputDir)
    except (OSError, IOError), e:
        print "Warn: %s" % e

    try:
        shutil.move("/data_fatso/xml_output/" + secondOutputFileName, projectOutputDir)
    except (OSError, IOError), e:
        print "Warn: %s" % e
else:
    try:
        shutil.move("/data_fatso/xml_output/" + firstOutputFileName, projectOutputDir)
    except (OSError, IOError), e:
        print "Warn: %s" % e

    try:
        shutil.move("/data_fatso/xml_output/" + secondOutputFileName, projectOutputDir)
    except (OSError, IOError), e:
        print "Warn: %s" % e
        
    try:
        shutil.move("/data_fatso/xml_output/" + thirdOutputFileName, projectOutputDir)
    except (OSError, IOError), e:
        print "Warn: %s" % e
                
    
