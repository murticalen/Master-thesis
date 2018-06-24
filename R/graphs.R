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

#__________      PIRMJER_TIMEPOINT_GAUSSA        _____________#
timePointExample <- read_csv2("./../examples/gauss-0-53-23.csv")
timePointExample <- as.data.frame(timePointExample)
timePointExample$i <- as.numeric(timePointExample$i) / 100000
intervalsExample$interval <- factor(intervalsExample$interval, levels = c("Poslije ponoći","Kasna noć","Rano jutro","Jutro","Prije podne","Podne","Poslije podne","Predvečerje","Večer","Kasna večer"))
timePointExample$v <- as.numeric(timePointExample$v)

timePointExample %>% filter (v > 0) %>% ggplot(aes (x = i, y = v, color = interval)) + geom_line()  + labs(title = "Primjer Gaussove funkcije za poziv u 0:53:23",  x = "Sat u danu", y = "Vrijednost funkcije pripadnosti", color = "Interval")
ggsave(filename = "./plots/gauss-0-53-23.png")

#__________      PRIMJER_INTERVALA        _____________#
intervalsExample <- read_csv2("./../examples/intervals-0-53-23.csv")
intervalsExample <- as.data.frame(intervalsExample)
intervalsExample$day <- factor(intervalsExample$day, levels = c ("Ponedjeljak", "Utorak", "Srijeda", "Četvrtak", "Petak", "Subota", "Nedjelja"))
intervalsExample$interval <- factor(intervalsExample$interval, levels = c("Poslije ponoći","Kasna noć","Rano jutro","Jutro","Prije podne","Podne","Poslije podne","Predvečerje","Večer","Kasna večer"))
intervalsExample$value <- as.numeric(intervalsExample$value)

intervalsExample %>% ggplot(aes (fill = interval, y = value, x = day)) + geom_histogram(stat = 'identity')  + labs(title = "Primjer intervalne dekompozicije za poziv u 0:53:23 u ponedjeljak",  x = "Sat u danu", y = "Vrijednost funkcije pripadnosti", fill = "Interval")
ggsave(filename = "./plots/intervals-0-53-23.png")

filter(intervalsExample, value > 0)
