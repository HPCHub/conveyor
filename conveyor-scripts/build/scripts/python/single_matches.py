#!/usr/bin/python

import sys
import string

file_in_name = "${input.m8.file}"
file_out_name = "${output.m8.file}"
file_in = open(file_in_name, 'r')
file_out = open(file_out_name, 'w')

lines_out = []
delta = 0
prevread = ''
while 1:
	line = file_in.readline()
	if not line: break
	if line.startswith('#'): continue
	words = line.split()
	if words[0] != prevread:
		#print line.rstrip('\n')
		file_out.write(line)
		prevread = words[0]

file_out.close()	
file_in.close()

