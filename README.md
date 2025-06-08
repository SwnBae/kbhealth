# HealthComfit - 건강 관리 소셜 플랫폼

## 1. 프로젝트 소개

### 개발 기간
- 2025.04.16 ~ 2025.05.28 (약 1개월)

### 프로젝트 내용
kbHealth는 개인의 식단과 운동을 기록하고 관리할 수 있는 종합 건강 관리 소셜 플랫폼입니다. 사용자는 자신의 건강 기록을 관리하고, 다른 사용자들과 소통하며, AI 기반 맞춤형 건강 추천을 받을 수 있습니다.

**주요 특징:**
- 개인별 맞춤 영양소 기준 제공
- 실시간 점수 및 랭킹 시스템
- 소셜 피드를 통한 커뮤니티 기능
- AI 기반 식단 추천 및 건강검진
- 실시간 채팅 및 알림 시스템

### 기술 스택

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA
- JWT Authentication
- MySQL 8.0
- WebSocket (STOMP)
- GPT API
- Gradle

**Frontend**
- Vue 3
- Vite

**Development Environment**
- IntelliJ IDEA
- Gradle Build Tool

**Infrastructure**
- AWS EC2 (t3.medium)
- Nginx (리버스 프록시)
- Let's Encrypt (SSL)
- Amazon Linux 2023

**Database**
- MySQL 8.0
- HikariCP Connection Pool

### 개발 인원
- 2명 (팀장 1명, 팀원 1명)

### 서비스 URL
- 메인 서비스: http://healthcomfit.site
- API 서버: http://healthcomfit.site/api

## 2. 주요 기능

### 회원가입 및 인증
- 회원가입 시 실시간 아이디/닉네임 중복 검사
- 입력값 유효성 검증
- JWT 토큰 기반 인증
- 자동 로그인 상태 확인

### 로그인 및 로그아웃
- 사용자 계정 정보로 로그인
- JWT 토큰을 HttpOnly 쿠키로 설정 (XSS 방지)
- 세션 종료 및 쿠키 삭제를 통한 안전한 로그아웃

### 식단 관리
- 음식 데이터베이스 기반 식단 기록
- 이미지 업로드 지원
- 영양소 자동 계산 및 분석
- 일자별 식단 기록 조회 및 검색
- 당일 이후 수정/삭제 제한

### 운동 관리
- 운동 종류, 시간, 칼로리 기록
- 운동 완료 상태 관리
- 이미지 업로드 지원
- 운동 기록 검색 및 필터링
- 당일 이후 수정/삭제 제한

### 점수 및 랭킹 시스템
- **일일 점수 계산:**
  - 칼로리: 권장량 ±10% 이내 시 +20점
  - 단백질: 권장량 90% 이상 시 +10점
  - 지방/당류/나트륨 초과 시 각각 -5점
  - 식이섬유: 권장량 이상 시 +5점
  - 운동 목표 달성 여부에 따른 가중치 적용
- **종합 점수:** 최근 10일간 일일 점수의 가중평균 (100점 만점)
- **랭킹:** 전체 사용자 및 팔로우 사용자 대상 랭킹 제공

### 소셜 피드
- 텍스트 및 이미지 포함 게시글 작성/수정/삭제
- 댓글 시스템 (페이지네이션 지원)
- 좋아요 기능 (토글 방식)
- 개별 사용자 피드 조회
- 게시글 상세 보기

### 팔로우 시스템
- 사용자 간 팔로우/언팔로우
- 팔로워/팔로잉 목록 조회
- 팔로우 시 실시간 알림 생성
- 자기 자신 팔로우 및 중복 팔로우 방지

### 실시간 알림 시스템
- WebSocket 기반 실시간 알림
- 댓글, 좋아요, 팔로우 활동 알림
- 알림 읽음/삭제 처리
- 읽지 않은 알림 개수 표시
- 일괄 알림 관리

### 실시간 채팅
- WebSocket(STOMP) 기반 1:1 채팅
- 채팅방 자동 생성
- 메시지 읽음 처리
- 읽지 않은 메시지 개수 표시
- 실시간 메시지 송수신

### 사용자 프로필 관리
- 프로필 이미지 및 정보 수정
- 신체 정보 관리 (키, 몸무게)
- 영양 달성도 및 최근 10일 점수 표시
- 팔로워/팔로잉 수 표시
- 사용자 검색 (닉네임/계정)

### AI 기반 맞춤형 추천
- GPT API를 활용한 개인화된 식단 추천
- 사용자의 성별, 몸무게, 영양 달성도 고려
- 현재 영양 상태 분석
- 권장 영양소 자동 계산

### AI 건강검진
- 최근 10일간 영양 달성률 종합 분석
- 사용자별 맞춤형 건강 상태 진단
- 개선이 필요한 영양소 및 생활습관 제안
- 장기적인 건강 관리 가이드 제공

### 건강 상태 분석
- 일일 영양소 섭취량 대비 권장량 달성률 계산
- 칼로리, 단백질, 지방, 탄수화물, 당류, 나트륨, 식이섬유 종합 분석
- 개인별 권장 영양소 자동 생성

## 3. 서비스 설계

### REST API 명세

#### 회원 인증 관련 (AuthController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 로그인 | POST | /api/auth/login |
| 로그아웃 | GET | /api/auth/logout |
| 회원가입 | POST | /api/auth/regist |
| 로그인 상태 확인 | GET | /api/auth/check |
| 아이디 중복 검사 | GET | /api/auth/check-account |
| 닉네임 중복 검사 | GET | /api/auth/check-username |

#### 소셜 피드 관련 (FeedController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 피드 조회 | GET | /api/feed |
| 개별 사용자 피드 조회 | GET | /api/feed/{member_account}/feed |
| 게시글 작성 | POST | /api/feed |
| 게시글 수정 | PUT | /api/feed/{post_id} |
| 게시글 삭제 | DELETE | /api/feed/{post_id} |
| 댓글 목록 조회 | GET | /api/feed/{post_id}/commentList |
| 댓글 작성 | POST | /api/feed/{post_id}/comment |
| 댓글 삭제 | DELETE | /api/feed/{post_id}/comment/{comment_id} |
| 좋아요 토글 | PUT | /api/feed/{post_id}/like |
| 게시글 상세 조회 | GET | /api/feed/post/{post_id} |

#### 기록 관리 (RecordController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 식단 목록 조회 | GET | /api/records/diet |
| 식단 생성 | POST | /api/records/diet |
| 식단 단건 조회 | GET | /api/records/diet/{drId} |
| 식단 수정 | PUT | /api/records/diet/{drId} |
| 식단 삭제 | DELETE | /api/records/diet/{drId} |
| 식단 검색 | GET | /api/records/diet/search |
| 권장 영양소 조회 | GET | /api/records/ns/{member_account} |
| 운동 목록 조회 | GET | /api/records/exercise |
| 운동 생성 | POST | /api/records/exercise |
| 운동 단건 조회 | GET | /api/records/exercise/{exId} |
| 운동 수정 | PUT | /api/records/exercise/{exId} |
| 운동 삭제 | DELETE | /api/records/exercise/{exId} |
| 운동 검색 | GET | /api/records/exercise/search |
| 운동 완료 처리 | PUT | /api/records/exercise/{exId}/complete |
| 운동 완료 해제 | PUT | /api/records/exercise/{exId}/uncomplete |

#### 알림 관리 (NotificationController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 페이징 알림 조회 | GET | /api/notifications/paged |
| 알림 읽음 처리 | PUT | /api/notifications/{notificationId}/read |
| 알림 삭제 | DELETE | /api/notifications/{notificationId} |
| 전체 알림 조회 | GET | /api/notifications |
| 읽지 않은 알림 조회 | GET | /api/notifications/unread |
| 읽지 않은 알림 개수 조회 | GET | /api/notifications/unread/count |
| 여러 알림 읽음 처리 | PUT | /api/notifications/read |
| 모든 알림 읽음 처리 | PUT | /api/notifications/read-all |
| 모든 알림 삭제 | DELETE | /api/notifications/all |

## 채팅 관련 (ChatController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 채팅방 목록 조회 | GET | /api/chat/rooms |
| 채팅 메시지 조회 | GET | /api/chat/rooms/{chatRoomId}/messages |
| 메시지 읽음 처리 | POST | /api/chat/rooms/{chatRoomId}/read |
| 읽지 않은 메시지 총 개수 조회 | GET | /api/chat/unread-count |

**WebSocket 메시지 매핑:**
| 기능 | 매핑 경로 |
|------|-----------|
| 메시지 전송 | /app/send-message |

## 팔로우 관리 (FollowController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 팔로우 | POST | /api/follow/following/{member_id} |
| 언팔로우 | DELETE | /api/follow/following/{member_id} |
| 팔로잉 목록 조회 | GET | /api/follow/followingList/{member_id} |
| 팔로워 목록 조회 | GET | /api/follow/followerList/{member_id} |

## AI 추천 및 건강검진 (GptController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| AI 식단 추천 | POST | /api/gpt/recommendDiet |
| AI 건강검진 | POST | /api/gpt/healthCheck |

## 음식 데이터베이스 관리 (ItemController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 전체 음식 목록 조회 | GET | /api/items |
| 음식 추가 | POST | /api/items |
| 특정 음식 조회 | GET | /api/items/{dietId} |
| 음식 정보 수정 | PUT | /api/items/{dietId} |
| 음식 삭제 | DELETE | /api/items/{dietId} |
| 음식 검색 | GET | /api/items/search |
| 음식 데이터 로드 | GET | /api/items/load-data |

## 랭킹 조회 (RankingController)
| 기능 | HTTP 메서드 | URI |
|------|-------------|-----|
| 전체 랭킹 조회 | GET | /api/ranking |
| 팔로잉 랭킹 조회 | GET | /api/ranking/following |


## 개발/테스트용 관리자 API (DummyDataController)
> ⚠️ **주의**: 개발 및 테스트 환경에서만 사용되는 API입니다. 프로덕션 환경에서는 보안을 위해 비활성화됩니다.

### 관리자 API 사용 목적
- **더미 데이터 생성**: 개발 환경에서 테스트용 대량 데이터 생성
- **데이터 계산**: 점수 시스템 및 랭킹 알고리즘 검증
- **시스템 모니터링**: 데이터베이스 상태 및 시스템 헬스체크

### WebSocket 통신 엔드포인트

**연결 설정:**
- WebSocket URL: `/ws`
- STOMP Prefix: `/app` (클라이언트 → 서버)
- Broker Prefix: `/topic`, `/queue` (서버 → 클라이언트)

**개인별 구독 경로:**
| 구독 경로 | 설명 |
|-----------|------|
| /user/queue/notifications | 개별 알림 수신 |
| /user/queue/notification-count | 안읽은 알림 개수 |
| /user/queue/notification-list-update | 알림 목록 업데이트 |
| /user/queue/chat-messages | 채팅 메시지 수신 |
| /user/queue/chat-room-update | 채팅방 정보 업데이트 |
| /user/queue/chat-unread-count | 안읽은 채팅 개수 |
| /user/queue/message-read-status | 메시지 읽음 상태 |

### 팀원 및 업무분담

**권순주 (팀장)**
- 로그인/회원가입 시스템 구현
- 게시판 및 소셜 피드 기능
- UI/UX 디자인
- 회원 관리 시스템
- 전반적인 오류 처리 및 예외 관리
- 웹소켓 로직(알림, 채팅) 및 호출 로직 최적화

**배수한 (팀원)**
- 식단 기록 관리 시스템
- 운동 기록 관리 시스템
- 점수 계산 및 랭킹 시스템
- 알림 채팅 시스템
- AI기반 API 기능
- 서버 배포 및 인프라 관리

### 배포 및 인프라 구성

**AWS EC2 환경:**
- 인스턴스 타입: t3.medium (2 vCPU, 4GB RAM)
- 운영체제: Amazon Linux 2023
- Java 런타임: OpenJDK 21
- 데이터베이스: MySQL 8.0
- 웹 서버: Nginx (리버스 프록시)

**보안 설정:**
- 방화벽: Amazon Linux firewalld 활성화
- 포트 제한: 22(SSH), 80(HTTP), 443(HTTPS), 3306(MySQL)
- JWT 토큰: HttpOnly 쿠키로 XSS 방지
- CORS: 프론트엔드 도메인만 허용
- SSL 인증서: Let's Encrypt

**성능 최적화:**
- Nginx gzip 압축 활성화
- 정적 파일 브라우저 캐싱
- MySQL HikariCP 연결 풀 설정
- JVM 메모리 최적화

---

## 추가 정보

### 예외 처리
사용자가 잘못된 값을 입력할 경우 적절한 Exception을 발생시키고, 각 예외에 맞는 유형별 오류 메시지를 처리합니다. (IllegalArgumentException, IllegalStateException 등)
