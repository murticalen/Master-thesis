userCluster <- read.csv2(file = './../dataset/dataCluster35.csv', stringsAsFactors = F)

profileFeatures <- read.csv2(file = './../dataset/centroids35.csv', stringsAsFactors = F)
profileFeatures$value <- as.numeric(profileFeatures$value)
group_by(profileFeatures, feature) %>% summarise(total = sum (value))

userProfiles <- inner_join(userCluster, profileFeatures)
sum(userProfiles$value)


expectedCalls <- read.csv2(file = './../cdr_output/1529100508884-expectedCalls.csv', stringsAsFactors = F)
expectedCalls$cnt <- as.numeric(expectedCalls$cnt)
group_by(expectedCalls, interval) %>% summarise(total = sum(cnt))
sum(expectedCalls$cnt)

#####

generatedCalls <- read.csv2(file = './../statistics/expected_call_count.csv', stringsAsFactors = F)
generatedCalls$expectedValue <- as.numeric(generatedCalls$expectedValue)
generatedCalls$difference <- generatedCalls$generatedCalls - generatedCalls$expectedTotalCalls
generatedCalls$percentageDifference <- (generatedCalls$difference) / generatedCalls$expectedTotalCalls * 100
ggplot(generatedCalls, aes (x = as.factor(expectedValue), y = percentageDifference)) + geom_boxplot()

group_by(generatedCalls, expectedValue) %>% summarise(totalDifference = sum(as.numeric(difference)), totalGroup = sum(as.numeric(expectedTotalCalls))) -> groupedDifference
groupedDifference$percentageDifference <- groupedDifference$totalDifference / groupedDifference$totalGroup * 100
groupedDifference

filter(groupedDifference, expectedValue < 10)

write.csv(groupedDifference, file = './../statistics/grouped_difference.csv')

###
user__profiles <- read.csv2(file = './../dataset/user_profiles.csv', stringsAsFactors = F)
user__profiles$totalProbability <- as.numeric(user__profiles$totalProbability)

group_by(user__profiles, dayIntervalId) %>% summarise(total = sum(totalProbability)) -> test
test$dayIntervalId <- test$dayIntervalId %% 10
group_by(test, dayIntervalId) %>% summarise(total = sum(total))
