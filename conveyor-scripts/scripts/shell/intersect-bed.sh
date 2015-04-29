inputAnnotationFile="${input.gff.file}";
inputBedFile="${input.bed.file}";
tmpOutFile="${project.build.dir}a.out";
outputNonExonReadsFile="${project.build.dir}non_exon_reads.txt";

#echo input.gff.file=${input.gff.file};
#echo input.bed.file=${input.bed.file};
#echo output.non-exon.file=${project.build.dir}non_exon_reads.txt;

#/data3/bio/biouser/tools/BEDTools-Version-2.16.2/bin/intersectBed -a $inputAnnotationFile -b $inputBedFile -sorted -wb > $tmpOutFile
#cat $tmpOutFile | egrep -v '(exon)|(cds)' | awk '{ print $13 }' > outputNonExonReadsFile