#read data
print(Sys.time())
callsData <- read.csv2(file = './../dataset/combined.csv', stringsAsFactors = F, nrow = 10000)
print(Sys.time())

#cast char time to date
callsData$callTime <- dmy_hms(callsData$callTime)

callsDataSample <- sample_n(callsData, 100000, r = T)

callsDataSample
