#read data
print(Sys.time())
callsData <- read.csv2(file = './../dataset/user_splitted/0.csv', stringsAsFactors = F)

callsData %>% group_by(callerId) %>% summarise(cnt = n()) %>% arrange(desc(cnt))

print(Sys.time())

#cast char time to date
callsData$callTime <- dmy_hms(callsData$callTime)

callsDataSample <- sample_n(callsData, 100000, r = T)

callsDataSample

userProfiles <- read.csv2(file = './../dataset/user_profiles.csv', stringsAsFactors = F)
str(userProfiles)
