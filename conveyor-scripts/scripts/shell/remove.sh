for FILE in ${!MAP_ARRAY[@]}; do
     rm --verbose "$FILE"
done