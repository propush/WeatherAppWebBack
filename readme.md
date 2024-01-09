# Weather App backend API

## Build

For unit tests run:
```
./gradlew test
```

For integration tests run:
```
./gradlew test -Dspring.profiles.active=inttest -DOPENWEATHERMAP_API_KEY=<your_api_key>
```
Your api key can be obtained from https://home.openweathermap.org/api_keys

To build run:
```
./gradlew build
```

## Frontend
Frontend can be found at https://github.com/propush/WeatherAppWebFront

## Example

The sample running (I hope so at least) installation can be found at https://weather.pumpmy.it
(sign up with any user, no additional confirmation required, no user data collected).
