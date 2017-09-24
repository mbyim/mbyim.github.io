---
layout: post
title: "Understanding and Visualizing Labor Force Participation Rates"
author: "Martin"
---

The World Bank collects data on a variety of world development indicators - everything from unemployment rates to health outcomes. I've used the data in the past for school projects, and always came away feeling like I had just scratched the surface, so I decided to use the [consolidated version of the dataset](https://www.kaggle.com/worldbank/world-development-indicators) on Kaggle to make a few visualizations.


Unemployment rate is the defacto measure of the labor market's health in the broader economy, but it has flaws: namely, as metrics tend to do, it [fails to capture the nuances](http://www.pewresearch.org/fact-tank/2017/03/07/employment-vs-unemployment-different-stories-from-the-jobs-numbers/) of the situation below the water's surface. One economic indicator that can bring more detailed understanding of the labor markets is the labor force participation rate. 



<center><img src="{{ site.url }}/assets/labor_rates.jpg" alt="Drawing" style="width: 500px;" /></center>


Labor force participation rates can often cause the unemployment rate to be [misleading](https://qz.com/877432/the-us-unemployment-rate-measure-is-deceptive-and-doesnt-need-to-be/) due it's role in [calculating unemployment statistics](https://www.bls.gov/cps/cps_htgm.htm#definitions). For example, if in the United States the number of employed people stays constant, but the number of people in the labor force decreases (read: labor force participation rate decreases), then the unemployment rate statistic would decrease. But does that mean the economic situation has improved? Not necessarily, when the labor force participation rate decreases it can be a sign that there are people who either can't or have given up [looking for work](https://www.theatlantic.com/business/archive/2016/06/the-missing-men/488858/).

<center><img src="{{ site.url }}/assets/labor_rates_geo.jpg" alt="Drawing" style="width: 750px;" /></center>


The labor force participation rate is a great example of why we shouldn't focus to heavily on just one metric (unemployment rates) beacuse when we do, we often fail to see the whole picture. And while labor force participation is an important part of understanding how people are interacting with the economy at large, we also shouldn't lionize this single metric either. Other metrics like [underemployment](https://www.bls.gov/lau/stalt.htm) can play a similar role in bringing a wholistic understanding of the labor market.


You can find the code for these two visualizations [here](https://github.com/mbyim/visualizations/blob/master/labor_plots.py). 