#OUTPUT=${MAP_ARRAY[$INPUT_CSFASTA_FILE]%%.*}

LABEL=${INPUT_CSFASTA_FILE%%.*}
LABEL=${LABEL##*/}
OUTPUT="${project.output.dir}/${LABEL}"
${solid-preprocess-filter-v2.path} -f "$INPUT_CSFASTA_FILE" \
    -g "$INPUT_QUAL_FILE" \
    -x off \
    -y off \
    -t on \
    -u 50 \
    -o "$OUTPUT" \
-v on