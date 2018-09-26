group_by(callsData, callerId) %>% summarise(callsCount = n()) -> callerCallsCount
totalCalls <- nrow(callsData)
group_by(callerCallsCount, callsCount) %>% summarise(cnt = n()) -> callCountCount
#callCountCount$cnt <- cumsum(callCountCount$cnt)

callCountCount %>% filter(callsCount < 5000) %>% ggplot(aes(x = callsCount, y = log(cnt))) + geom_point()

group_by(callerCallsCount, callsCount) %>% summarise(cnt = n()) %>% filter(callsCount >= 5000)

#+ geom_bar(stat = 'identity')

#################TRAJANJE POZIVA

group_by(callsData, duration) %>% summarise(cnt = n()) %>%  ggplot(aes(x = duration, y = (cnt))) + geom_point()  + labs(title = "Učestalost trajanja poziva",  x = "Trajanje poziva u sekundama", y = "Broj poziva s tim trajanjem")
ggsave(filename = "./plots/duration-distr.png")

group_by(callsData, duration) %>% summarise(cnt = n()) %>% filter(duration < 1000) %>% ggplot(aes(x = duration, y = (cnt))) + geom_point()  + labs(title = "Učestalost trajanja poziva",  x = "Trajanje poziva u sekundama", y = "Broj poziva s tim trajanjem")
ggsave(filename = "./plots/duration-distr-1000.png")

group_by(callsData, duration) %>% summarise(cnt = n()) %>% arrange(desc(cnt))

filter(callsData, duration > 25000) %>% select(callerId:weekDay) %>% arrange(callTime) -> callDurationOutliers
callDurationOutliers

max(callsData$callerId, callsData$receiverId)

count(callsData)

length(unique(callsData$receiverId))

unique(callsData$callerId) -> uniqueCallers
unique(callsData$receiverId) -> uniqueReceivers
intersect(uniqueCallers, uniqueReceivers) -> uniqueCallersReceivers
length(uniqueCallersReceivers)

group_by(callsData, callerId, receiverId) %>% summarise(callsCount = n()) %>% group_by(callsCount) %>% summarise(cnt = n()) %>% arrange(desc(cnt))

############SOCIAL NETWORK
group_by(callsData, callerId, receiverId) %>% summarise(linkedCount = n()) -> callerReceiverCount
group_by(callsData, callerId) %>% summarise(personalCount = n()) -> callerCount

filter(callerCount, personalCount <= 5)

inner_join(callerReceiverCount, callerCount) -> callerReceiverCount
callerReceiverCount$linkage <- callerReceiverCount$linkedCount * callerReceiverCount$linkedCount / callerReceiverCount$personalCount 
callerReceiverCount %>% arrange(desc(linkage))
