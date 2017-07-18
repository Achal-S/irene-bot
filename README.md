Serverless chatbot as an AWS lambda service for the Irene project
====================
## Inspiration
A motorbike is much more than a transportation, is a passion and a way of life. Bikers fall in love with their motorbikes and give them nicknames hoping that these two wheels will one day return their unconditional love. With modern technology this is finally possible blending together serverless chatbots, Internet of Things and artificial intelligence. The result of this mix is **Irene: the motorbike bot**.

Irene is the first smart chatbot installed on your motorbike and powered by AWS Lex that let you interact with your motorbike wherever you are, whenever you want through standard messaging channels (**Slack, Facebook, Skype**).

## What it does
Irene provides several services to her owners. 
- Irene allows GPS based geo-localization of the vehicle (e.g., **"where are you?"**) useful in case you forget where you parked or if the motorbike get stolen.
- Irene makes your motorbikes loyal to you. Indeed she is a smart alarm system with text-to-speech capabilities that push back anyone who touches or moves the vehicle (e.g., **"set alarm"**).
- Irene cares about you because -through smart sensors- detects motorbike falls and alerts with a text a predefined contact reporting the accident location. This allows fast medical responses that may even save lives (e.g., **"set safety mode"**).
- Irene is a en expert when it comes to motorbikes. Indeed she embeds an extensible expert system that let you troubleshoot the most common mechanical problems you may experience. (e.g., **"motorbike is faulty"**).

## How I built it
Irene is composed by three main pieces of technology. 
- First, an on-board computer (a **RaspberryPi**) equipped with several smart sensors (GPS, accelerometers, temperature, humidity, and pressure) and a 3G modem connecting her to the web. This state of the art internet-of-things setting exposes a layer of API and proactively interacts with the AWS web services on the Web.
- The second main component of the architecture is a set of AWS Lambda deployed in the cloud. These serverless components contain the intelligence of the bot and several auxiliary functionalities (e.g., the integration with messaging channels).
- In addition to these two main components, Irene's architecture also relies on other AWS services like Polly, AWS Api Gateway, and AWS CloudWatch. Finally, several third-party libraries and APIs have been used as well (e.g., Skebby, Google Maps, Openweather, etc).

Attached to this submission there are two links to two public GitHub repositories:
- [irene-bot](https://github.com/geeordanoh/irene-bot): contains all the code deployed in the on AWS
- [irene-embedded](https://github.com/geeordanoh/irene-embedded): contains all the code deployed on the on-board computer

## Challenges I ran into
In this project I definitely explored technologies out of my engineering comfort zone:
- Blending seamlessly mobile hardware and software in the Cloud has been the major challenge I faced. Indeed building a reliable, ubiquitous  and secure connection between the motorbike and the Cloud has not been easy. I solved this challenge by relying on a reverse and self-connecting SSH tunnel over 3G between the motorbike and an EC2 instance on AWS  that acts as a proxy of the motorbike in the Cloud.
- A second major challenge has been represented by the extension of Lex implemented to support proactive messages to the user (e.g., when the alarm is triggered) and to support messaging channels not natively supported by Lex (e.g., Skype).
- A third significant challenge is the following. Internet of things applications typically present two limitations: power and physical space. Irene is no exception. I had to carefully minimize the physical space occupied by the on-board computer given the limited room on the motorbike. In addition I had to carefully choose the power system and design the software to make a savvy use of the battery that, at the current stage, lasts for very long hours (if necessary Irene could be even plugged to the motorbike battery).

## Accomplishments that I'm proud of
In the last two months I bought a motorbike, took the license to drive it, and I have designed and implemented Irene all by myself through endless and sleepless nights of coding. My real girlfriend still hates me for that but in the end this is a tribute to her, being her name **Irene**.

The final result makes me extremely proud of myself. I still remember the first time the bot replied to me, it was late in the night and nothing seemed to work at that time. Suddenly, after yet another upload to AWS Lambda, the bot replied. In that precise moment I knew I could make it. **Why did I do it?** I did it just because I am en eager learner and I desperately wanted to go to **reInvent 2017**.

## What I learned
Never used so many AWS services in my life like in the last two months, some of them for the very first time (Lex, CloudWatch, ...). Not only I learned them as the documentation describe but I also learned tip and tricks that only real world projects may teach. For example, I learned how to improve AWS lambda performances by keeping their environment *warm* through periodical invocations by AWS CloudWatch rules. In addition, this was my very first Internet of Things project, so also that area was new to me.

## What's next for Irene the motorbike bot
Irene's journey just began. For example, I am already designing a over-the-air update system that let me instantaneously deploy each new release of the software to the on-board computer. I also envision additional features that make Irene social. For example Irene may record your best trips and share them with other Irene instances. Finally, multiple Irene installations may even communicate with each other to share data about traffic jams or quality of the road.
