from cStringIO import StringIO
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.converter import TextConverter
from pdfminer.layout import LAParams
from pdfminer.pdfpage import PDFPage
from os import listdir
from os.path import isfile, join
import re


def convert(fname, pages=None):
    if not pages:
        pagenums = set()
    else:
        pagenums = set(pages)

    output = StringIO()
    manager = PDFResourceManager()
    converter = TextConverter(manager, output, laparams=LAParams())
    interpreter = PDFPageInterpreter(manager, converter)

    infile = file(fname, 'rb')
    for page in PDFPage.get_pages(infile, pagenums):
        interpreter.process_page(page)
    infile.close()
    converter.close()
    text = output.getvalue()
    output.close
    return text 


def convertPlain(fname, pages=None):
    if not pages:
        pagenums = set()
    else:
        pagenums = set(pages)

    output = StringIO()
    manager = PDFResourceManager()
    converter = TextConverter(manager, output, laparams=None)
    interpreter = PDFPageInterpreter(manager, converter)

    infile = file(fname, 'rb')
    for page in PDFPage.get_pages(infile, pagenums):
        interpreter.process_page(page)
    infile.close()
    converter.close()
    text = output.getvalue()
    output.close
    return text 




def extractNums(text):
    return re.findall(r"[-+]?\d*\.\d+|\d+",text)

def getGPA(nums):
    numbers = re.findall(r"[-+]?\d*\.\d+|\d+",nums)
    return numbers[0]

def getScore():
    return 1.0




myPath = "./sampleResume"
outputList = []
onlyFiles = [ f for f in listdir(myPath) if isfile(join(myPath,f)) ]
for singleFile in onlyFiles:
    plainText = convertPlain(myPath +"/"+ singleFile)
    gpaText = plainText.upper().find("GPA")
    print(getGPA(plainText[gpaText:gpaText + 20]))

#plainText = (convertPlain('document.pdf'))
#styleText = convert('document.pdf')
#gpaText = plainText.upper().find("GPA")
#print(getGPA(plainText[gpaText:gpaText + 20]))
