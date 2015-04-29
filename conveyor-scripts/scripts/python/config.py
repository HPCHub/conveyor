#
# Temporary unused script disigned for read and manage
# db configuration file tnsnames.ora
#


import re


class Config:


	db = None


	def getDatabase(self):

		if (self.db is None):
			self.db = Config.Database()

		return self.db


	class Database:


		tnsnamesOra = '/etc/tnsnames.ora'
		regexpOra   = '^(\w+?)\s?=.*?HOST\s?=\s?(.+?)\).*?PORT\s?=\s?(\d+?)\).*?SERVICE_NAME\s?=\s?(.+?)\)'
		connections = {}


		def __init__(self):

			tnsnames = open(self.tnsnamesOra, 'r').read()

			for match in re.finditer(self.regexpOra, tnsnames, re.M + re.S):
				matches = match.groups()

				self.connections[matches[0]] = {
					'host'    : matches[1],
					'port'    : matches[2],
					'service' : matches[3],
					'url'     : '%s:%s/%s' % matches[1:]
				}

		def getConnection(self, name):

			return self.connections[name]
