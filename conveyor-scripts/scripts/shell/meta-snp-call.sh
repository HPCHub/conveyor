set_num=${input.bact-id.prop}


SAMTOOLS="${samtools.path}"
VARSCAN="${varscan.path}"
PYTHON="${python.path}"


scriptdir="/data1/bio/runs-kovarskiy/scripts/snp_scripts/"
workdir="/data1/bio/metagenome_snp_pipe_results/$set_num/"
ref=${input.reference.file}
contigs_list="$workdir/contig_set"


#Maybe need to rm pileup files
while read bam; do
	short_name=$(basename "$bam" .bam)
	#But it needs to remove sorted.alignment.
	pileup=$workdir/$short_name.pileup
	$SAMTOOLS mpileup -f $ref -l $contigs_list $bam > $pileup
	$PYTHON $scriptdir/MakeFormat.py $pileup
	rm -f $pileup
done < "${input.bam-list.file}"

date
find $workdir -name "*.info"  > $workdir/infos.list
date
$PYTHON $scriptdir/GeneCoverProfile.py $workdir/infos.list $workdir/short_anno.tsv
date
$PYTHON $scriptdir/GeneStat.py $workdir $set_num $workdir/infos.list
date
$PYTHON $scriptdir/DistanceNonsym_2.1.py $workdir/infos.list -D -C 8 -L 25000 -p all_genes_
date