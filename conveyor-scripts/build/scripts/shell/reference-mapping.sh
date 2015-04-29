PATHNAME="${input.csfasta.file}"
FILENAME=$(basename "$PATHNAME")
EXTENSION="${FILENAME##*.}"

if [[ $EXTENSION == "fa"  || $FILENAME =~ \.fa\. ]]; then
     KEY="-f"
elif [[ $EXTENSION == "fasta" || $FILENAME =~ \.fasta\. ]]; then
    KEY="-f"
elif [[ $EXTENSION == "fastq" || $FILENAME =~ \.fastq\. ]]; then
    KEY="-q"
elif [[ $EXTENSION == "csfasta" || $FILENAME =~ \.csfasta\. ]]; then
    KEY="-f -C"
else
    while read LINE; do
        if [[ $LINE == ">"* ]]; then
            read LINE
            COUNT=$(echo "$LINE" | grep -cP '^T[0-9\.]+$')
            if [[ $COUNT > 0 ]]; then
                #echo "csfasta"
                KEY="-f -C"
            else
                #echo "fasta"
                KEY="-f"
            fi 
            break
        elif [[ $LINE == "@"* ]]; then
            #echo "fastq"
            KEY="-q"
            break
        else
            KEY=""
            echo "Cannot sense bowtie input file format key"
        fi
    done < $PATHNAME
fi 

LOG_FILE="${project.log.dir}/reference-mapping.log";
TMP_FILE="${output.sam.file}.tmp"
${bowtie.path} $KEY -S -t -v 3 -k 1 ${bowtie.thread} "${input.reference.index}" "${input.csfasta.file}" "$TMP_FILE" 2>"$LOG_FILE"
awk -F'\t' '{if($4 != 0){print;}}' "$TMP_FILE" > "${output.sam.file}"
rm "$TMP_FILE"