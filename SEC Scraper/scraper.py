# This script will download files from the SEC. 
# Use the CIK of the company, which type of files are required as well as from which date onward you want the files from.

 
from bs4 import BeautifulSoup 
import re
import requests
import os
 
class SecCrawler():
  
    def make_directory(self, symbol, cik, filing_type, priorto):
        # Making the directory to save comapny filings
        if not os.path.exists("SECdata/"):
            os.makedirs("SECdata/")
        if not os.path.exists("SECdata/"+str(symbol)):
            os.makedirs("SECdata/"+str(symbol))
        if not os.path.exists("SECdata/"+str(symbol)+"/"+str(cik)):
            os.makedirs("SECdata/"+str(symbol)+"/"+str(cik))
        if not os.path.exists("SECdata/"+str(symbol)+"/"+str(cik)+"/"+str(filing_type)):
            os.makedirs("SECdata/"+str(symbol)+"/"+str(cik)+"/"+str(filing_type))
   
    def save_in_directory(self, symbol, cik, priorto, docs, names, filing_type):
        # Save every text document into its respective folder
        for i in range(len(docs)):
            link = docs[j]
            source = requests.get(link)
            text = source.text
            path = "SEC-Edgar-data/"+str(symbol)+"/"+str(cik)+"/"+str(filing_type)+"/"+str(names[j])
            filename = open(path,"a")
            filename.write(text)   


    def get_filing(self, symbol, filing_type, cik):
        try:
            self.make_directory(symbol, cik, priorto, filing_type)
        except:
            print("Unable to create directory")

        url = "http://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK="+str(cik)+"&type=" + str(filing_type) + "&dateb="+str(priorto)+"&owner=exclude&output=xml&count="+str(count)
        list_of_links = [] # List of links
        print("Getting " + str(symbol) + " " + filing_type)
        source = requests.get(url)
        text = r.text
        soup = BeautifulSoup(text)
        links = soup.find_all('filinghref')
        for item in links:
            link = links.string
            if link.string.split(".")[len(link.string.split(".")) - 1] == "htm": # Checks if the file is htm
                link += "l"  # If so convert to html
                list_of_links.append(link)

        total = len(list_of_links)

        print("Downloading %s files", total)

        docs = []
        names = []

        for i in range(total):
            request = str(list_of_links[i])[0:total - 11]
            doc = request + ".txt"
            name = request.split("/")[len(txtdoc.split("/")) - 1]
            docs.append(doc)
            names.append(name)

        try:
            self.save_in_directory(symbol, cik, priorto, docs, names, str(filing_type))
        except:
            print("Error encountered ")
 
        print ("Successfully downloaded all the files")

