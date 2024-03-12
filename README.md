![Platform](https://img.shields.io/badge/Platform-Android-orange.svg)
![API](https://img.shields.io/badge/API-23%2B-green.svg)
[![JitPack](https://jitpack.io/v/yourssu/YLS-Android.svg)](https://jitpack.io/#yourssu/YLS-Android)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## YLS란

YLS는 숭실대학교 동아리 유어슈에서 사용하는 로깅 시스템입니다.

사용자의 로그는 Logger queue에 담기며, 로그가 10개 쌓였을 경우/사용자가 이탈할 경우 백엔드 API를 호출해 로그를 전송합니다.

사용자의 userId는 YLS 내에서 식별 불가능 한 값으로 처리됩니다.

## Installation

#### Gradle Project

```kts
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

#### Add Dependency
```kts
dependencies {
    implementation("com.github.yourssu:YLS-Android:latest-version")
}
```

최신 버전은 [release](https://github.com/yourssu/YLS-Android/releases)에서 확인할 수 있습니다.

## Docs

https://yourssu.github.io/YLS-Android/docs/0.x/

## Usage


## YLS를 사용하는 프로덕트

Soomsil-Android (private)

## 타 버전 저장소

[YLS-iOS](https://github.com/yourssu/YLS-iOS)

[YLS-Web](https://github.com/yourssu/YLS-Web)

[YLS-Backend](https://github.com/yourssu/YLS-Backend)
