#read data
print(Sys.time())
callsData <- read.csv2(file = './../dataset/combined.csv', stringsAsFactors = F)
callsData$callTime <- ymd_hms(callsData$callTime)

callsData$hour <- hour(callsData$callTime)
callsData$weekDay <- as.factor(callsData$weekDay)

callsData$day <- day(callsData$callTime)
callsData$month <- month(callsData$callTime)

####GRAF PO DANU
callsData %>% group_by(weekDay, day, month) %>% summarise(cnt = n()) %>% group_by(weekDay) %>% summarise(weekDayCount = n()) -> weekDayCount
callsData$linearTime <- (as.integer(callsData$weekDay) - 1)*24 + callsData$hour
group_by(callsData, linearTime, weekDay) %>% summarise(cnt = n()) -> hourlyCalls
inner_join(hourlyCalls, weekDayCount) -> hourlyCalls
hourlyCalls$cnt <- hourlyCalls$cnt / hourlyCalls$weekDayCount
hourlyCalls %>% ggplot(aes(x = linearTime, y = cnt)) + geom_bar(stat = 'identity', aes(fill = (weekDay))) + labs(title = "Ukupni broj poziva po satima",  x = "Sat u danu", y = "Broj poziva", fill = "Dan u tjednu") + theme(text = element_text(size=11), axis.text.x=element_blank())
ggsave(filename = "./plots/daily_call_count.png")


callsData %>% group_by(weekDay) %>% summarise(cnt = n()) -> dailyCalls
inner_join(dailyCalls, weekDayCount) -> dailyCalls
dailyCalls$cnt <- dailyCalls$cnt / dailyCalls$weekDayCount

dailyCalls

callsData %>% group_by(callerId, receiverId) %>% summarise(cnt = n()) %>% arrange(desc(cnt)) -> social_network

sn <- sample_n(social_network, 1000, replace = T)
g <- graph_from_data_frame(d = sample_n(social_network, 1000), directed = FALSE)

par(mar=c(0,0,0,0))
plot(g)

print(Sys.time())

#cast char time to date
callsData$callTime <- dmy_hms(callsData$callTime)

callsDataSample <- sample_n(callsData, 100000, r = T)

callsDataSample

userProfiles <- read.csv(file = './../dataset/user_profiles.csv', stringsAsFactors = F, sep = ';')
userProfiles$totalProbability <- as.numeric(userProfiles$totalProbability)
filter(userProfiles, dayIntervalId < 11) %>% spread(dayIntervalId, totalProbability) -> userProfilesForClustering
select(userProfilesForClustering, userId) -> users
select(userProfilesForClustering, -userId) -> userProfilesForClustering

kmeans(userProfilesForClustering, centers = 50, iter.max = 500) -> kmeans

# kMeansTester <- function(data, k, kMax) {
#   if(k < kMax) {
#     res <- kMeansTester(data, k+1, kMax)
#     c(kmeans(data, centers = k, iter.max = 150), res)
#   } else {
#     c()
#   }
# }
# 
# kMeansTest <- kMeansTester(userProfilesForClustering, 3, 30)
# sapply(kMeansTest, function(x){
#   print(x)
# })

ggplot(monday_kmeans_java, aes(x = as.factor(V1), y = V2)) + geom_point()
daily_kmeans_java$day <- seq(1, 7, 1)
ggplot(daily_kmeans_java, aes(x = as.factor(day), y = V2)) + geom_point()

kmeans$centers %>% as.data.frame() %>% gather()

test <- data.frame(users, kmeans$cluster)
colnames(test) <- c("userId", "cluster")
ggplot(test, aes(x = cluster)) + geom_bar()

userProfiles %>% group_by(dayIntervalId) %>% summarise(total = sum(as.numeric(totalProbability))) -> probabilitiesGroupedByDayInterval
userProfiles %>% group_by(userId) %>% summarise(total = sum(as.numeric(totalProbability))) -> probabilitiesGroupedByUser

length(unique(userProfiles$userId))

probabilitiesGroupedByUser %>% group_by(total) %>% summarise(cnt = n()) %>% ggplot(aes(x = total, y = cnt)) + geom_point()

max(userProfiles$userId)


