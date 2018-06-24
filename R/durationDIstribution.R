test <- function()

callsData %>% sample_n(10000) -> sample

quantile(sample$duration, c(0))

callsData %>% group_by(callerId) %>% summarise(
  c000 = quantile(duration, c(0)),
  c125 = quantile(duration, c(.125)),
  c250 = quantile(duration, c(.250)),
  c375 = quantile(duration, c(.375)),
  c500 = quantile(duration, c(.500)),
  c625 = quantile(duration, c(.625)),
  c750 = quantile(duration, c(.750)),
  c875 = quantile(duration, c(.875)),
  c1000 = quantile(duration, c(1.00))
  ) -> durationQuantilles

write.table(file = './../dataset/duration.csv', durationQuantilles, row.names = F, dec = ".", sep = ";")

callsData$duration %>% quantile(c(.125))

group_by(callsData, duration) %>% summarise(cnt = n()) %>% arrange(duration)
