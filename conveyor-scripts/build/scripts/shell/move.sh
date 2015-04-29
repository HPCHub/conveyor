MOVE_TO_BASEDIR=${move.to.basedir}
for FILE in ${!MAP_ARRAY[@]}; do
    if $MOVE_TO_BASEDIR; then
        DESTINATION=$(dirname "$FILE")
    fi 

    mv --verbose "$FILE" "$DESTINATION"
done