#
#SOLiD(TM) & ION Accuracy Enhancement Tool (SAET) wrapper.
#

FIXED_CSFASTA_FILE=$(basename "${input.csfasta.file}");
LOG_FILE="${project.log.dir}/saet.log"

${saet.path} "${input.csfasta.file}" 200000000 -qual "${input.qual.file}" -qvupdate -trustprefix 25 -localrounds 3 -globalrounds 2 ${saet.thread} -log "$LOG_FILE"
cp "$PWD/fixed/$FIXED_CSFASTA_FILE" "${output.csfasta.file}"
cp "$PWD/fixed/none" "${output.qual.file}"
rm -r "$PWD/fixed";