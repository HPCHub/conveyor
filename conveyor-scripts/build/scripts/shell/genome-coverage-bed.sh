OUTPUT_DIR="${project.output.dir}";
TMP_DIR="${OUTPUT_DIR}/tmp/";

mkdir $TMP_DIR;

${bedtools.path}/genomeCoverageBed -ibam "${input.bam.file}" -g "${stat.ref-genome.file}" > "${output.coverage.file}"
### BED-tools outputs histogram:
### seq name - coverage - number of positions - seq length - fraction in histogram(0-1)

# get bp_coverage and pos_coverage (i.e. coverage breadth and width)
echo "Computing bp_, pos_coverage";
python ${coverage-histogram.path} "${output.coverage.file}" "${stat.ref-genome.file}" "${output.bp-coverage.file}" "${output.pos-coverage.file}";

# combine coverage with prepared FASTA_ITEM_ID list (both alphabetical)
# bp:
BP_COV_TMP_FILE="$TMP_DIR$(basename "${output.bp-coverage.file}").tmp";
echo "Attaching FASTA_ITEM_IDs"
paste "${output.bp-coverage.file}" "${stat.item-id.file}" > "$BP_COV_TMP_FILE";
echo 'checking that FASTA_ITEM_ID list contains same names as coverage...';
awk '{if($1 != $3){print "Incorrect FASTA_ITEM_ID list!";}}' "$BP_COV_TMP_FILE";
echo 'done.';
awk '{if($2 != 0){gsub(/ /, "\t"); print $1 "\t" $2 "\t" $4;}}' "$BP_COV_TMP_FILE" > "$OUTPUT_DIR/bp_cov_id.txt";
# pos:
POS_COV_TMP_FILE="$TMP_DIR$(basename "${output.pos-coverage.file}").tmp";
paste "${output.pos-coverage.file}" "${stat.item-id.file}" > "$POS_COV_TMP_FILE";
echo 'checking that FASTA_ITEM_ID list contains same names as coverage...';
awk '{if($1 != $3){print "Incorrect FASTA_ITEM_ID list!";}}' "$POS_COV_TMP_FILE";
echo 'done.';
awk '{if($2 != 0){gsub(/ /, "\t"); print $1 "\t" $2 "\t" $4;}}' "$POS_COV_TMP_FILE" > "$OUTPUT_DIR/pos_cov_id.txt"

rm -r "$TMP_DIR";