/data3/bio/lifescope_bin/lifescope/xsq/XSQConverter/bin/XSQConverter.sh \
--mode=LMP \
--c1="${input.first-csfasta.file}"  --q1="${input.first-qual.file}" \
--c2="${input.second-csfasta.file}" --q2="${input.second-qual.file}" \
--libraryInsertSizeMinimum=1000 \
--libraryInsertSizeMaximum=3000 \
--xsqfile=${output.xsq.file} \
--libraryName=${project.name} \
--laneNumber=1 \
--runStartTime="`date +"%Y-%m-%d %H:%m:%S"`"