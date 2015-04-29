#:remove comments within fasta file
#:input.fasta.file

SCRIPT_NAME=$(basename "$BASH_SOURCE");
echo "=> Start $SCRIPT_NAME";

### debug-section ####
echo "input.fasta.file=${input.fasta.file}";
######################
#
#
#
#### exec-section ####
sed -i '/#/d' "${input.fasta.file}"
######################

echo "uptime: $SECONDS seconds";
echo "=> Stop $SCRIPT_NAME";