DIRECTORY="${project.output.dir}"
for PATHNAME in ${!MAP_ARRAY[@]}; do
    BASENAME=$(basename "$PATHNAME")
    FILENAME="${BASENAME%.*}"
    EXTENSION="${BASENAME##*.}"

    if [ $EXTENSION == "gz" ]; then 
        gunzip --stdout "$PATHNAME" > "$DIRECTORY/$FILENAME"
    elif [ $EXTENSION == "bz2" ]; then
        bunzip2 --keep --decompress --stdout "$PATHNAME" > "$DIRECTORY/$FILENAME"
    elif [ $EXTENSION == "rar" ]; then
        unrar x "$PATHNAME" "$DIRECTORY"
    elif [ $EXTENSION == "zip" ]; then
        unzip "$PATHNAME" -d "$DIRECTORY"
    else
        cp --verbose "$PATHNAME" "$DIRECTORY"
    fi
done
