#!/usr/bin/python
import os
import sys

## 0 - AA CC GG TT
## 1 - AC CA GT TG
## 2 - AG GA CT TC
## 3 - AT TA CG GC
def cs_seq2na_seq(cs_seq):
    colormap = {'0': {'A':'A','C':'C','G':'G','T':'T'},\
                '1': {'A':'C','C':'A','G':'T','T':'G'},\
                '2': {'A':'G','G':'A','C':'T','T':'C'},\
                '3': {'A':'T','T':'A','C':'G','G':'C'}}
    init_na = cs_seq[0]
    na = [init_na]
    for cs_bit in cs_seq[1:]:
        next_na = colormap[cs_bit][init_na]
        na.append(next_na)
        init_na = next_na
    return ''.join(na)

filename_csfasta = "${input.csfasta.file}"
if( not os.access(filename_csfasta,os.R_OK) ):
    print "%s is not accessible"%filename_csfasta
    sys.exit(1)

# filename_fasta = filename_csfasta+'_BASESPACE'
filename_fasta = "${output.csfasta.file}"

f_csfasta = open(filename_csfasta,'r')
f_fasta = open(filename_fasta,'w')
iter_csfasta = iter(f_csfasta)
for line_csfasta in iter_csfasta:
    if( line_csfasta.startswith('#') ):
        continue

    if( line_csfasta.startswith('>') ):
        header = line_csfasta.strip()
        cs_seq = iter_csfasta.next().strip()

        if( cs_seq.find('.') > 0 ):
            continue
        
        na_seq = cs_seq2na_seq(cs_seq)
        f_fasta.write("%s\n%s\n"%(header,na_seq))

f_fasta.close()
f_csfasta.close()
