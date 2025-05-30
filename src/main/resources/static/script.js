const baseUrl = "https://minnow-perfect-humbly.ngrok-free.app";
let currentUserSurvey = null;
let matchingResults = [];

function showMessage(msg, isError = false) {
  const el = document.getElementById('message');
  el.textContent = msg;
  el.style.color = isError ? 'red' : 'green';
}

// 회원가입
document.getElementById('registerForm').addEventListener('submit', async e => {
  e.preventDefault();
  const form = e.target;
  const data = {
    username: form.username.value,
    password: form.password.value,
    name: form.name.value
  };
  try {
    const res = await fetch(`${baseUrl}/auth/register`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Registration failed');
    const json = await res.json();
    showMessage(`Registered: ${json.name}`);
    form.reset();
  } catch (err) {
    showMessage(err.message, true);
  }
});

// 로그인
document.getElementById('loginForm').addEventListener('submit', async e => {
  e.preventDefault();
  const form = e.target;
  const data = {
    username: form.username.value,
    password: form.password.value
  };
  try {
    const res = await fetch(`${baseUrl}/auth/login`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      credentials: 'include',
      body: JSON.stringify(data)
    });
    if (!res.ok) {
      let msg = 'Login failed';
      try {
        const errJson = await res.json();
        if (errJson && errJson.message) msg = errJson.message;
      } catch {}
      throw new Error(msg);
    }
    const json = await res.json();
    onLoginSuccess(form.username.value);
    form.reset();
  } catch (err) {
    showMessage(err.message, true);
  }
});

// 로그인 성공시
async function onLoginSuccess(name) {
  showMessage(`Welcome, ${name}`);
  document.getElementById('profile').style.display = 'block';
  document.getElementById('tabsContainer').style.display = 'block';
  loadProfile();
  loadSurveys();
  loadMySurveys();
}

// 프로필 로드
async function loadProfile() {
  try {
    const res = await fetch(`${baseUrl}/auth/profile`, {method: 'GET', credentials: 'include'});
    if (!res.ok) {
      let msg = 'Failed to load profile';
      try {
        const errJson = await res.json();
        if (errJson && errJson.message) msg = errJson.message;
        if (errJson && errJson.code === 'NOT_LOGGED_IN') {
          document.getElementById('profile').style.display = 'none';
          document.getElementById('tabsContainer').style.display = 'none';
        }
      } catch {}
      throw new Error(msg);
    }
    const user = await res.json();
    document.getElementById('profileInfo').innerHTML =
      `<p>ID: ${user.username}</p><p>Name: ${user.name}</p>`;
  } catch (err) {
    showMessage(err.message, true);
  }
}

// 모든 설문조사 로드
async function loadSurveys() {
  try {
    const res = await fetch(`${baseUrl}/surveys/list`, {
      method: 'GET',
      credentials: 'include'
    });
    if (!res.ok) {
      let msg = 'Failed to load surveys';
      try {
        const errJson = await res.json();
        if (errJson && errJson.message) msg = errJson.message;
        if (errJson && errJson.code === 'NOT_LOGGED_IN') {
          document.getElementById('profile').style.display = 'none';
          document.getElementById('tabsContainer').style.display = 'none';
        }
      } catch {}
      throw new Error(msg);
    }
    const list = await res.json();
    const ul = document.getElementById('surveyListContent');
    ul.innerHTML = '';
    list.forEach(s => {
      const li = document.createElement('li');
      if (s.mine) li.classList.add('mine');
      li.innerHTML = `
        <h3>${s.user.name}님 (${s.user.username})</h3>
        <p>기숙사: ${s.dormName} | 청소: ${s.cleanLevel}회/주 | 흡연: ${s.smoking ? '예' : '아니오'}</p>
        <p>${s.etc}</p>
        <div class="meta">${s.createdAt} ${s.mine ? '(내 설문)' : ''}</div>
      `;
      ul.appendChild(li);
    });
  } catch (err) {
    showMessage(err.message, true);
  }
}

// 내 설문조사 목록 가져오기
async function loadMySurveys() {
  try {
    const res = await fetch(`${baseUrl}/surveys/my`, {
      method: 'GET',
      credentials: 'include'
    });
    if (!res.ok) {
      let msg = 'Failed to load my surveys';
      try {
        const errJson = await res.json();
        if (errJson && errJson.message) msg = errJson.message;
      } catch {}
      throw new Error(msg);
    }
    const mySurveys = await res.json();
    const container = document.getElementById('myMatchingSurveys');
    container.innerHTML = '';
    
    if (mySurveys.length === 0) {
      container.innerHTML = '<p>작성한 설문이 없습니다. 먼저 설문을 작성해주세요.</p>';
      document.getElementById('matchButton').disabled = true;
      return;
    }
    
    mySurveys.forEach(survey => {
      const div = document.createElement('div');
      div.className = 'survey-item';
      div.innerHTML = `
        <div class="content">
          <h3>${survey.user.name}님 (${survey.user.username})</h3>
          <p>기숙사: ${survey.dormName} | 청소: ${survey.cleanLevel}회/주 | 흡연: ${survey.smoking ? '예' : '아니오'}</p>
          <p>${survey.etc}</p>
          <div class="meta">${survey.createdAt}</div>
        </div>
        <input type="radio" name="selectedSurvey" value="${survey.id}" class="survey-checkbox">
      `;
      container.appendChild(div);
    });
    
    document.querySelectorAll('input[name="selectedSurvey"]').forEach(radio => {
      radio.addEventListener('change', () => {
        document.getElementById('matchButton').disabled = false;
        currentUserSurvey = mySurveys.find(s => s.id == radio.value);
      });
    });
    
    document.getElementById('matchButton').addEventListener('click', handleMatching);
  } catch (err) {
    showMessage(err.message, true);
  }
}

// 매칭 처리
async function handleMatching() {
  const selectedSurvey = document.querySelector('input[name="selectedSurvey"]:checked');
  if (!selectedSurvey) {
    showMessage('매칭할 설문을 선택해주세요', true);
    return;
  }
  
  const surveyId = selectedSurvey.value;
  try {
    showMessage('매칭 중입니다...', false);
    const res = await fetch(`${baseUrl}/surveys/matching?id=${surveyId}`, {
      method: 'GET',
      credentials: 'include'
    });
    if (!res.ok) {
      throw new Error('매칭 요청에 실패했습니다.');
    }
    const results = await res.json();
    matchingResults = results;
    showMatchingResults(results);
  } catch (err) {
    showMessage(err.message, true);
  }
}

function showMatchingResults(results) {
  document.getElementById('myMatchingSurveys').style.display = 'none';
  document.getElementById('matchButton').style.display = 'none';
  const resultsDiv = document.getElementById('matchingResults');
  resultsDiv.classList.add('active');
  resultsDiv.style.display = 'block';
  document.getElementById('resultsCount').textContent = `(${results.length})`;
  if (results.length === 0) {
    document.getElementById('matchResultsGrid').style.display = 'none';
    document.getElementById('noResults').style.display = 'block';
  } else {
    document.getElementById('matchResultsGrid').style.display = 'grid';
    document.getElementById('noResults').style.display = 'none';
    renderMatchingResults(results);
  }
}

// 매칭 결과 렌더링
function renderMatchingResults(results) {
  const grid = document.getElementById('matchResultsGrid');
  grid.innerHTML = '';
  results.forEach(result => {
    grid.appendChild(createMatchCard(result));
  });
}

// 매칭 카드 생성
function createMatchCard(result) {
  const card = document.createElement('div');
  card.className = 'match-card';
  const matchRate = Math.round(result.matchingRate);
  let rateClass = 'medium';
  if (matchRate >= 80) rateClass = 'high';
  else if (matchRate < 60) rateClass = '';
  const dormMatch = currentUserSurvey && currentUserSurvey.dormName === result.dormName;
  const cleanMatch = currentUserSurvey && currentUserSurvey.cleanLevel === result.cleanLevel;
  const smokingMatch = currentUserSurvey && currentUserSurvey.smoking === result.smoking;
  card.innerHTML = `
    <div class="match-card-header">
      <div class="match-card-title">
        <h3>${result.user.name}</h3>
        <span class="match-rate ${rateClass}">${matchRate}% 일치</span>
      </div>
      <div class="match-details">
        <div class="detail-row">
          <div class="detail-label">기숙사:</div>
          <div class="detail-value">
            <span>${result.dormName}</span>
            <div class="match-icon ${dormMatch ? 'match' : 'no-match'}"></div>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-label">청소 횟수:</div>
          <div class="detail-value">
            <span>주 ${result.cleanLevel}회</span>
            <div class="match-icon ${cleanMatch ? 'match' : 'no-match'}"></div>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-label">흡연 여부:</div>
          <div class="detail-value">
            <span>${result.smking ? '흡연' : '비흡연'}</span>
            <div class="match-icon ${smokingMatch ? 'match' : 'no-match'}"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="additional-info">
      <div class="label">기타 정보:</div>
      <div class="text">${result.etc || '추가 정보가 없습니다.'}</div>
    </div>
    <div class="match-card-footer">
      <button class="contact-btn" onclick="contactUser('${result.user.name}', '${result.user.username}')">
        연락하기
      </button>
    </div>
  `;
  return card;
}

// 연락하기 기능
function contactUser(name, username) {
  alert(`${name}님(${username})에게 연락 요청을 보냈습니다.`);
}

// 뒤로가기 버튼
document.getElementById('backToMatching').addEventListener('click', () => {
  document.getElementById('matchingResults').classList.remove('active');
  document.getElementById('matching').style.display = 'block';
});

// 정렬 기능
document.getElementById('sortOptions').addEventListener('change', (e) => {
  const sortBy = e.target.value;
  let sortedResults = [...matchingResults];
  switch(sortBy) {
    case 'match':
      sortedResults.sort((a,b) => b.matchingRate - a.matchingRate);
      break;
    case 'recent':
      sortedResults.sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt));
      break;
    case 'dorm':
      if (currentUserSurvey) {
        sortedResults.sort((a,b) =>
          (b.dormName === currentUserSurvey.dormName ? 1 : 0)
          - (a.dormName === currentUserSurvey.dormName ? 1 : 0)
        );
      }
      break;
  }
  renderMatchingResults(sortedResults);
});

// 로그아웃
document.getElementById('logoutBtn').addEventListener('click', async () => {
  await fetch(`${baseUrl}/auth/logout`, { method:'POST', credentials:'include' });
  showMessage('Logged out');
  document.getElementById('profile').style.display = 'none';
  document.getElementById('tabsContainer').style.display = 'none';
});

// 탭 기능 초기화
document.querySelectorAll('.tab-btn').forEach(button => {
  button.addEventListener('click', () => {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    button.classList.add('active');
    document.getElementById(button.getAttribute('data-tab')).classList.add('active');
  });
});

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', () => {
  // 설문조사 제출
  const surveyFormEl = document.getElementById('surveyFormElement');
  if (surveyFormEl) {
    surveyFormEl.addEventListener('submit', async e => {
      e.preventDefault();
      const data = {
        dormName: e.target.dormName.value,
        cleanLevel: parseInt(e.target.cleanLevel.value),
        smoking: e.target.smoking.checked,
        etc: e.target.etc.value
      };
      try {
        const res = await fetch(`${baseUrl}/surveys`, {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          credentials: 'include',
          body: JSON.stringify(data)
        });
        if (!res.ok) {
          const errText = await res.text();
          console.error('Server error response:', errText);
          throw new Error(`Survey submission failed with status ${res.status}`);
        }
        const result = await res.json();
        const smokingText = result.smoking ? '예' : '아니오';
        alert(
          `${result.user.name}님, ${result.dormName}, 청결도: ${result.cleanLevel}, 흡연: ${smokingText}, 기타: ${result.etc} 
성공적으로 제출 되었습니다.`
        );
        e.target.reset();
        loadSurveys();
        loadMySurveys();
      } catch (err) {
        console.error('Submit Survey Error:', err);
        showMessage(err.message, true);
      }
    });
  }

  // 탭 기능 초기화 (중복 방지)
  const tabButtons = document.querySelectorAll('.tab-btn');
  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      tabButtons.forEach(btn => btn.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
      button.classList.add('active');
      document.getElementById(button.getAttribute('data-tab')).classList.add('active');
    });
  });
});