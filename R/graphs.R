nf <- gaussian()
nf
str(nf)

gauss <- function(x) {
  exp(-x*x / 2) / sqrt(2 * pi);
}

gauss2 <- function(x, mu = 0, sigma = 1) {
  gauss((x-mu)/sigma) / sigma
}

time <- seq(from = -5, to = 29, by = 0.01)

gaussTest <- expand.grid(time, c(13))
colnames(gaussTest) <- c("time", "mu")
gaussTest$value <- gauss2(gaussTest$time, gaussTest$mu, sigma = 1)
filter(gaussTest, value > 0.001) -> gaussTest

ggplot(gaussTest, aes(x = time, color = as.factor(mu), y = value)) + geom_point() -> graph
graph
ggsave(filename = "./plots/gauss_test.png", plot = graph)

test<- data.frame(runif(800, 0, 1), runif(800, 0, 1))
colnames(test) <- c("x", "y")
select(test, c(x, y)) %>% kmeans(centers = 12) -> kmeans
test$clusterR <- kmeans$cluster
ggplot(test, aes(x = as.numeric(x), y = as.numeric(y), color=as.factor(clusterR))) + geom_point()

ggplot(intervals_count, aes (x = V1, y = V2)) + geom_point()

#__________      PLACEHOLDER_INTENZITETI        _____________#
intensities <- list()
intensities$time <- seq(0, 23, by = 1)
intensities$change <- runif(intensities$time, -1, 5)
intensities$value <- dnorm(intensities$time, 11.5, 3) * 150 + intensities$change
intensities$value <- ifelse(intensities$value > 0, intensities$value, 0)
intensities <- as.data.frame(intensities)

ggplot(intensities, aes (x = time, y = value)) + geom_bar(stat = 'identity') + labs(title = "Primjer intenziteta poziva",  x = "Sat u danu", y = "Očekivani broj poziva") + theme(text = element_text(size=17))
ggsave(filename = "./plots/caller_intensities_idea.png")

#__________      PLACEHOLDER_INTENZITETI        _____________#