DB_PFASTA_RAP="/data3/bio/metagenome/reference/ARDB1_1/resisGenes_no_dfr.rapsearchdb"
${rapsearch.path} -q "$INPUT_FASTA_FILE" -d $DB_PFASTA_RAP -o "${project.output.dir}" ${rapsearch.thread}
mv "${PWD}/output.m8" "${project.output.dir}"
rm "${PWD}/output.aln"