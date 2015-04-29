OUTPUT_FILE_PREFIX="${project.output.dir}/${output.bam-file.prefix}.sorted"
${samtools.path} sort "${input.bam.file}" "$OUTPUT_FILE_PREFIX";