inputFile=${input.bam.file}
outputFile=${inputFile/%sort.bam/bed}

#echo input.bam.file=$inputFile;
#echo output.bed.file=$outputFile;

#/data3/bio/biouser/tools/BEDTools-Version-2.16.2/bin/bamToBed -i $inputFile > $outputFile