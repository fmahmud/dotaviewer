import urllib2

developer_key = 'E2469B841908CBF830EC652B7BAB5D71'
for i in range(   10000000) :
	match_id = 9200000000
	url = 'https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key={{KEY}}&match_id={{MID}}'
	url = url.replace("{{KEY}}", developer_key)
	url = url.replace("{{MID}}", str(match_id+i))
	response = urllib2.urlopen(url)
	html = response.read()
	f = open('json/'+str(match_id+i)+'.json', 'w')
	f.write(html)
	print "Opened match number "+ str(match_id+i)


