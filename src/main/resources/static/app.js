const baseUrl = "https://minnow-perfect-humbly.ngrok-free.app"; // ngrok 주소로 변경

function showMessage(msg, isError = false) {
  const el = document.getElementById('message');
  el.textContent = msg;
  el.style.color = isError ? 'red' : 'green';
}

//회원가입
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

//로그인
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
      // 401 등 에러 응답일 때 서버 메시지 추출
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

// 로그인 성공시 -> profile창, survey창 로드
async function onLoginSuccess(name) {
  showMessage(`Welcome, ${name}`);
  document.getElementById('profile').style.display = 'block';
  document.getElementById('tabsContainer').style.display = 'block';
  loadProfile();
  loadSurveys();
  loadMySurveys();
}

//프로필창
async function loadProfile() {
  try {
    const res = await fetch(`${baseUrl}/auth/profile`, {method : 'GET', credentials: 'include'});
    if (!res.ok) {
      // 서버에서 JSON 에러 메시지 반환 시 처리
      let msg = 'Failed to load profile';
      try {
        const errJson = await res.json();
        if (errJson && errJson.message) msg = errJson.message;
        // 로그인 안됨 코드면 UI 숨김
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

//모든 설문조사 load
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

//내 설문조사 목록 가져오기
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
    
    // 하나의 설문조사가 선택되었을때만 제출버튼 활성화
    const radioButtons = document.querySelectorAll('input[name="selectedSurvey"]');
    radioButtons.forEach(radio => {
      radio.addEventListener('change', () => {
        document.getElementById('matchButton').disabled = false;
      });
    });
    
    // 매칭 버튼
    document.getElementById('matchButton').addEventListener('click', handleMatching);
  } catch (err) {
    showMessage(err.message, true);
  }
}

// Handle the matching process
function handleMatching() {
  const selectedSurvey = document.querySelector('input[name="selectedSurvey"]:checked');
  if (!selectedSurvey) {
    showMessage('매칭할 설문을 선택해주세요', true);
    return;
  }
  
  const surveyId = selectedSurvey.value;
  showMessage(`설문 #${surveyId}로 룸메이트 매칭을 요청했습니다.`, false);
  
  // Here you would normally send this to a backend API
  // For demonstration purposes, we're just showing a success message
  alert(`설문 #${surveyId}에 대한 매칭이 요청되었습니다. 매칭 결과는 추후 안내됩니다.`);
}

//로그아웃
document.getElementById('logoutBtn').addEventListener('click', async () => {
  await fetch(`${baseUrl}/auth/logout`, {method: 'POST', credentials: 'include'});
  showMessage('Logged out');
  //프로필창, 탭컨테이너 가리기
  document.getElementById('profile').style.display = 'none';
  document.getElementById('tabsContainer').style.display = 'none';
});

//설문조사 제출
document.addEventListener('DOMContentLoaded', () => {
  const surveyFormEl = document.getElementById('surveyForm');
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
        // 새 설문을 추가한 뒤 목록 새로고침
        loadSurveys();
        loadMySurveys();
      } catch (err) {
        console.error('Submit Survey Error:', err);
        showMessage(err.message, true);
      }
    });
  }
});

// Tab functionality
document.addEventListener('DOMContentLoaded', () => {
  const tabButtons = document.querySelectorAll('.tab-btn');
  
  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      // Remove active class from all buttons and content
      tabButtons.forEach(btn => btn.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
      });
      
      // Add active class to clicked button
      button.classList.add('active');
      
      // Show corresponding content
      const tabId = button.getAttribute('data-tab');
      document.getElementById(tabId).classList.add('active');
    });
  });
});