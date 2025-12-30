# be-was-2025
코드스쿼드 백엔드 교육용 WAS 2025 개정판


# 1주차 초기 계획
현 상황 기준으로 각 단계별 문제는 어떻게 진행할지, debugging challenge 진행 여부 등을 고민해봤습니다.

## 계획

- 1일차 : 기초 세팅 및 1주차 초기 계획 수립
- 2일차 : mac이랑 친해지기 & 1단계 구현 및 학습 완료 ⇒ 공유까지 진행되면 좋을 것 같습니다.
- 3일차 : 2단계, 3단계 구현 및 학습 완료
- 4일차 : 학습 내용을 토대로 한 가지 주제 정해서 깊게 공부해보고 공유
- 5일차 : 4일차 내용 추가 진행 및 개선점 파악해보기

debug 챌린지는 java 사용 경험이 있는 입소자는 필수가 아니라고 하셨기 때문에 일단 보류해두고, 진행하겠습니다.

# 정리

## gradle 내부 구조

https://m.blog.naver.com/sqlpro/222666588911

```bash
.
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── App.java
    │   └── resources
    └── test
        ├── java
        │   └── AppTest.java
        └── resources
```

- build.gradle
  Gradle 기본 빌드 설정용 스크립트 파일이다. 프로젝트의 빌드 처리 및 의존성에 대한 설정이 작성되어 있다.
- gradle 디렉토리
  Gradle 환경을 래핑한 wrapper 파일들이 저장된다.
- gradle/wrapper/gradle-wrapper.properties
  프로젝트를 빌드에 사용할 gradle 버전을 포함하여, gradle 실행에 필요한 설정 정보가 기록된다. 다른 버전의 gradle을 사용하여 프로젝트를 빌드하고 싶다면 이 파일의 내용을 수정하면 된다.
- gradlew
  macOS 및 Linux에서 실행하기 위한 gradle 구현체이다.
- gradlew.bat
  Windows용 gradle 구현체이다.

- settings.gradle
  프로젝트 설정 정보를 담은 파일이다. 프로젝트 명 및 서브 프로젝트 정보 등이 모두 기록된다.
- src 디렉토리
  Gradle 프로젝트에서 사용하는 소스 코드나 리소스, 테스트 코드 등의 파일이 저장된다. main과 test라는 2개의 폴더가 포함된다.
- src/main 디렉토리
  애플리케이션 구현에 필요한 소스 코드를 저장하는 디렉토리이다. 언어별로 하위 디렉토리가 구분되며, 기본적으로 App.java 파일이 포함된다.
- src/test 디렉토리
  애플리케이션 테스트에 필요한 단위 테스트 파일을 모아두는 디렉토리이다. 여기에 작성된 내용은 빌드시에 포함되지 않지만, CI/CD 파이프라인에서는 중요한 역할을 한다.

gradle : 여러 모듈로 구성되는 java 프로젝트를 구조화하고, 테스트/빌드/실행 과정을 쉽게 진행할 수 있도록 도와주는 task 관리와 종속 라이브러리 관리 등을 단순화 하는 빌드 툴

jar

java 프로젝트를 빌드함으로써 생성되는 산출물. jar 파일에는 실행 가능한 jar파일이 있고, library 형태의 실행 불가한 jar 파일이 있습니다.

### `./gradlew run` vs `java -jar app.jar`

각각의 의미는

- Gradle 설정에 정의된 실행 task를 통해
  컴파일, classpath 구성, 의존성 로딩 등
  **실행에 필요한 과정을 포함하여 JVM을 기동한다.**
- 이미 빌드되어서 산출된, 실행 가능한 jar 파일을

  **JVM이 직접 실행한다.**


<aside>
💡

Visual studio에서 실행하는 C++ 프로젝트로 예를 든다면,

`./gradlew run` 는 IDE에서 debug 실행하는 것

`java -jar app.jar` 는 빌드된 exe파일을 눌러 직접 실행하는 것과 유사합니다.

</aside>

## plugin 정리

https://kotlinworld.com/323

### Plugin이란?

Gradle Task의 집합

구글이나 IDE 개발사인 JetBrains에서 미리 만들은 다음 Plugin 형태로 묶어놓으면,

저희는 해당 plugin을 가져다 사용할 수 있습니다.

```
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
```

### gradle task란?

https://developerpearl.tistory.com/101

task : 빌드로 수행되는 하나의 독립적인 유닛

**Task의 분류**

1. Actionable  Task : 액션이 있는 task로 **compileJava**와 같은 task

2. Lifecycle task : 액션이 없는 task로 **assemble, build**와 같은 task

dependsOn과 같은 구조를 통해 다른 task에 의존적인 task도 생성할 수 있습니다.