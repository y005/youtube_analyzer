# Youtube Analyzer

### 프로젝트 설명
다양하고 많은 유튜브 채널들을 구독한 이후로 채널의 많은 영상들이 계속해서 매일 올라오고 쌓이기 때문에 영상의 컨텐츠를 직접 클릭해서 보지 않아도 영상에 대한 반응과 주제를 빠르게 파악할 수 있도록 자동 분석해주는 프로젝트를 기획하게 되었습니다.

### 주요 기능
- OAuth 2.0을 활용한 사용자의 구독 채널 정보 자동 수집
- JWT를 활용한 인증/인가 API 제공
- 구독한 채널들의 영상 정보 분석 제공
  1. 영상 통계 분석 지표 제공
     - 구독자 변화 추이
     - 좋아요 비율
     - 조회수
  2. 댓글 분석 지표 제공
     - 긍부정 비율
     - 주요 키워드
     
[//]: # (  3. 유사한 키워드 영상 검색 제공)

### 사용 기술
- 유튜브 정보 분석 API 서버 사이드 환경
  - Spring Boot
  - MyBatis
  - MySQL 5.7
  - Spring Security
  - Youtube Data Client Library
  
    [//]: # (  - Redis)
- 댓글 분석 API 서버 사이드 환경
  - Flask
  - Nltk, Konlpy
  - Tensorflow
- CI/CD
  - Github Actions
  - Docker
- Cloud
  - AWS EC2

[//]: # (  - AWS OpenSearch)
