/data3/bio/lifescope_bin/lifescope/bin/jsub.sh -s "http://cluster.ripcm.com:9998/lifescope" -u jsub -w P7tt_f6AAK <<EOF
cd /projects
mk ${project.type}
cd ${project.type}
mk ${project.name}
cd ${project.name}
set workflow genomic.resequencing.lmp
# define the input
add xsq ${input.xsq.file}
# specify the reference to be used
set reference ${input.reference.file}
set annotation.dbsnp.file /data3/bio/hsa/reference/hg18/dbSNP_b130.tab
set annotation.gtf.file /data3/bio/hsa/reference/hg18/refGene.gtf
set analysis.filter.reference /data3/bio/hsa/reference/hg18/human_filter_reference.fasta
# these commands turn off the secondary modules
set saet.run 0 secondary/saet.ini
# these commands turn off the tertiary modules
set cnv.run 0 tertiary/cnv.ini
set inversion.run 0 tertiary/inversion.ini
set large.indel.run 0 tertiary/large.indel.ini
set small.indel.run 0 tertiary/small.indel.ini
set annotation.run 0 tertiary/annotation.cnv.ini
set annotation.run 0 tertiary/annotation.large.indel.ini
set annotation.run 0 tertiary/annotation.small.indel.ini
run
wait
exit
EOF