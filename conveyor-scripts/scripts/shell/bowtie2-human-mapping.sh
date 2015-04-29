PATHNAME="${input.fastq.file}"
FILENAME=$(basename "$PATHNAME")
EXTENSION="${FILENAME##*.}"

if [[ $EXTENSION == "fa"  || $FILENAME =~ \.fa\. ]]; then
     KEY="-f"
elif [[ $EXTENSION == "fasta" || $FILENAME =~ \.fna\. ]]; then
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

#if [ -f "$INPUT_QUAL_FILE" ]; then
#    echo "Skip $(basename $BASH_SOURCE) due quality file absent"
#fi

${bowtie2.path} $KEY -t -D 15 -R 2 -N 0 -L 20 -i S,1,0.75 --local ${bowtie.thread} -x "${input.human-reference.index}" "${input.fastq.file}" -S /dev/null --un "${output.fastq.file}"

