#!bin/sh
exit
#NE_READS="../P/non_exon_reads_moxp"
NE_READS=${input.non-exon.file}
echo "NE_READS: $NE_READS";
NE_READS_F3=$NE_READS"_F3";
echo "NE_READS_F3: $NE_READS_F3"

ls $NE_READS

READS="${input.fasta.file}"
echo "READS: $READS";

# get reads directory name
#NE_READS_FN="${NE_READS##*/}"
#NE_READS_DIR="${NE_READS%$NE_READS_FN}"
NE_READS_DIR=${project.build.dir}
echo "NE_READS_DIR: $NE_READS_DIR";

ls $READS

# add _F3 to reads 
awk '{ print $0"_F3" }' $NE_READS > $NE_READS_F3

# select

SEL=$NE_READS_F3".csfasta"
echo "SEL: $SEL";
SELQ=$NE_READS_F3"_QV.qual"
echo "SELQ: $SELQ";

perl selectSeqs.pl -f $NE_READS_F3 $READS > $SEL

echo "selected"

UNCOLOR="/data3/bio/biouser/tools/denovo_preprocessor_v2.2.1/denovo_preprocessor_solid_v2.2.1.pl"

perl "$UNCOLOR" -run fragment -f3 $SEL -dir $NE_READS_DIR
rm $NE_READS_DIR"cs_fragment_input.csfasta"
mv $NE_READS_DIR"de_fragment_input.de" $NE_READS_F3".fa"

# translate
python translatedna.py $NE_READS_F3".fa" > $NE_READS_F3"_AMIN.fa"