callsData <- read.csv2(file = './../dataset/combined.csv', stringsAsFactors = F)
#cast char time to date
callsData$callTime <- dmy_hms(callsData$callTime)

weekdays(callsData$callTime, abbreviate = T) %>% factor(levels=c("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")) -> callsData$weekDay




callsDataSample <- sample_n(callsData, 100000, r = T)