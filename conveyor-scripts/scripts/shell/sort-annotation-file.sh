inputFile=${input.gff.file}
outputFile=${inputFile/%gff3/sort.gff3}

#echo input.gff.file=$inputFile;
#echo output.gff.file=$outputFile;

#sort -k1,1 -k4,4n $inputFile > $outputFile