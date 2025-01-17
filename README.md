<div align="center">
  
<img src="https://github.com/user-attachments/assets/bae54899-5a36-4aa0-a6b5-ca7a7fecae80" width="200px" height="200px"/>
  
## STORYTELLER

`#사용자 맞춤형 동화 생성` `#학습에 편리한 기능` <br /> <br />
기존에 없는 새로운 영어 동화를 위해 원하는 키워드를 통해 다양하고 창의적인 영어 동화를 경험을 통해, <br /> 영어 동화에 대한 어린이들의 흥미와 관심을 유발할 수 있는 <br />
**어린이들을 위한 창의적인 영어 동화 앱**, STORYTELLER입니다.

_written by pyounani_

</div>

### Introduction

> 덕성여자대학교 컴퓨터공학전공 개발 소모임 Corner 2024년 제3회 코너 팀 프로젝트 <br />
> 2024 ICT멘토링 한이음 공모전 - 입선🏆<br />
> 개발 기간: 2024.02 ~ 2024.11

### Link

> Figma: [Go to Figma Project](https://www.figma.com/design/vn0MM3w33Qt2pbOeCYeevr/UI%2FUX?node-id=0-1&t=qMLo9GoMnD6dtonh-1) <br />
> API Docs: [Go to API Docs](https://band-blackberry-aca.notion.site/70fe441ebe0a41aab3995c4fd57c262e?v=f05b03bdfb834dbcb86494e4c31d1ece) <br />

### Repository

> Client: https://github.com/DS-StoryTeller/front-end <br />
> Spring boot server: https://github.com/DS-StoryTeller/back-end <br/>

<br />

## Table of contents

- [Team, cojac](#team-cojac)
  - [About us](#about-us)
- [Preview](#preview)
- [Tech stack](#tech-stack)
  - [Service Architecture](#service-architecture)
  - [Infrastructure Architecture](#infrastructure-architecture)
- [Features](#features)

<br />

## Team, cojac!

| <img src="https://github.com/user-attachments/assets/5075e5e6-36a5-4dd4-b44f-9eb49f1a19d1" width="90px" height="90px" alt="김은서"/> | <img src="https://github.com/user-attachments/assets/52b12c2e-0342-46d9-82e1-cbca7c68d6d0" width="90px" height="90px" alt="류지예"/> | <img src="https://github.com/user-attachments/assets/d1624659-ba9f-4db1-9688-3dc163492d51" width="90px" height="90px" alt="박유나"/> | <img src="https://github.com/user-attachments/assets/7e05b089-0ec0-4fdf-b61f-94ff1d42f1ef" width="90px" height="90px" alt="조예영"/> | <img src="https://github.com/user-attachments/assets/d49f7d26-cfd7-4475-89e8-a6f9a652f5a5" width="90px" height="90px" alt="편도나"/> |
| :--------------------------------------------------------------: | :--------------------------------------------------------------: | :--------------------------------------------------------------: | :--------------------------------------------------------------: | :--------------------------------------------------------------: |
|                           김은서(팀장)                           |                           류지예(팀원)                           |                           박유나(팀원)                           |                           조예영(팀원)                           |                           편도나(팀원)                           |
|             [@7beunseo](https://github.com/7beunseo)             |          [@devdaradara](https://github.com/devdaradara)          |             [@pyounani](https://github.com/pyounani)             |           [@sylvia1213](https://github.com/sylvia1213)           |             [@dona0123](https://github.com/dona0123)             |
|                             Backend                              |                             Backend                              |                             Backend                              |                             Frontend                             |                            Full Stack                            |

### About Us

> [Go to Ground Rule](https://band-blackberry-aca.notion.site/GROUND-RULE-df39be0a0e1241dcb2e00a04c4a13f88?pvs=73) <br />
> [Go to Github convention](https://band-blackberry-aca.notion.site/GIT-Convention-6f4b1dc7ddfa43e2bfcf5e513d358796?pvs=73) <br />
> [Go to Backend convention](https://band-blackberry-aca.notion.site/1e6aa4929bd841459614b75a2c35df5d?pvs=73)

<br />

## Preview
![편집본  StoryTeller 배속 자막 편집본 4K](https://github.com/user-attachments/assets/61a1313c-95c0-4f5f-b9a8-cf1d00a59f3c) <br />
[Go to YouTube Link](https://www.youtube.com/watch?v=9JHMy-bQO-Y)

<br />

## Tech stack

### Service Architecture
![서비스 구성도](https://github.com/user-attachments/assets/24059f87-4542-4cb2-989e-bf710dde687e)

### Infrastructure Architecture

<br />

## Features
![image-6](https://github.com/user-attachments/assets/ff481524-1675-4ad1-80b1-fd05a759cfb1) <br />

- 동화 생성 및 학습

  - 동화 제작을 위한 주제 입력<br />
    사용자가 음성 또는 키보드로 동화 주제를 입력할 수 있습니다. 음성 인식이 잘 되지 않으면 다시 입력을 요청합니다.

  - 입력된 주제가 적절한지 검증<br />
    사용자가 입력한 주제가 적절한지 확인하여 표시합니다.

  - 동화 생성<br />
    사용자가 입력한 주제를 바탕으로 동화를 생성하며, 책의 난이도는 프로필에 등록된 나이에 맞추어 조정됩니다.

<br />

![image-7](https://github.com/user-attachments/assets/9580fe6a-8afd-47c3-872b-43048e3a658e)

<br />

- 동화 읽기 및 설정

  - 동화 읽어주기<br />
    동화를 음성으로 읽어주며, 이전에 읽은 페이지부터 계속 읽어줍니다.

  - 동화 진행 상황 표시<br />
    프로그레스 바를 통해 동화 진행 상황을 표시합니다.

  - 음성 출력 정지 및 재생<br />
    사용자가 원할 때 음성 출력을 정지하거나 재생할 수 있습니다.

  - 동화 설정 변경<br />
    동화 읽기 속도와 텍스트 크기를 조정할 수 있습니다.

- 단어 학습

  - 모르는 단어 뜻 제공<br />
    동화를 들으며 모르는 단어의 뜻과 예문을 확인할 수 있는 기능을 제공합니다.

  - 모르는 단어 저장 및 취소<br />
    모르는 단어는 하이라이팅되며, 다시 책을 읽을 때 하이라이트를 취소하거나 뜻을 확인할 수 있습니다.

<br />

![image-8](https://github.com/user-attachments/assets/9e6c1e63-f741-4f35-ad7c-58dae7f4bc51)

- 책 조회 및 퀴즈

  - 책 조회 필터링<br />
    책 리스트를 즐겨찾기, 현재 읽고 있는 책 등으로 필터링할 수 있으며, 각 책은 제목, 저자, 표지 이미지와 함께 표시됩니다.

  - 퀴즈 생성<br />
    동화를 다 읽은 후 아동의 창의적 사고를 자극하는 퀴즈를 제공합니다. 퀴즈는 음성으로 응답 가능하며, 대답 후 칭찬 도장을 제공합니다.

<br />
