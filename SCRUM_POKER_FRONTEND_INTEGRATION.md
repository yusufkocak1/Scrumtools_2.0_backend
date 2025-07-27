# Scrum Poker Frontend Entegrasyon Rehberi

## Genel Bakış

Bu dokümanda, Scrum Poker sisteminin frontend ile nasıl entegre edileceği açıklanmaktadır. Sistem WebSocket tabanlı gerçek zamanlı iletişim kullanır.

## Gerekli Kütüphaneler

### React için
```bash
npm install socket.io-client
npm install @stomp/stompjs
npm install sockjs-client
```

### Vue.js için
```bash
npm install socket.io-client
npm install @stomp/stompjs
npm install sockjs-client
```

## WebSocket Bağlantısı

### 1. Bağlantı Kurma

```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class PokerWebSocketService {
  constructor() {
    this.client = null;
    this.isConnected = false;
    this.subscriptions = new Map();
  }

  connect(token) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      onConnect: (frame) => {
        console.log('WebSocket bağlantısı kuruldu:', frame);
        this.isConnected = true;
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      },
      onWebSocketError: (event) => {
        console.error('WebSocket error:', event);
      }
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.isConnected = false;
      this.subscriptions.clear();
    }
  }

  subscribeToTeamPoker(teamId, callback) {
    if (!this.isConnected) {
      console.error('WebSocket bağlantısı yok');
      return;
    }

    const destination = `/topic/poker/team/${teamId}`;
    const subscription = this.client.subscribe(destination, (message) => {
      const data = JSON.parse(message.body);
      callback(data);
    });

    this.subscriptions.set(`poker-${teamId}`, subscription);
    return subscription;
  }

  unsubscribeFromTeamPoker(teamId) {
    const key = `poker-${teamId}`;
    const subscription = this.subscriptions.get(key);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(key);
    }
  }

  sendJoinMessage(teamId, userId, userName) {
    if (!this.isConnected) return;

    this.client.publish({
      destination: `/app/poker/team/${teamId}/join`,
      body: JSON.stringify({
        type: 'USER_JOINED',
        userId: userId,
        userName: userName
      })
    });
  }

  sendLeaveMessage(teamId, userId, userName) {
    if (!this.isConnected) return;

    this.client.publish({
      destination: `/app/poker/team/${teamId}/leave`,
      body: JSON.stringify({
        type: 'USER_LEFT',
        userId: userId,
        userName: userName
      })
    });
  }
}

export default new PokerWebSocketService();
```

## API Endpoints

### 1. REST API Kullanımı

```javascript
class PokerApiService {
  constructor(baseUrl, authToken) {
    this.baseUrl = baseUrl;
    this.authToken = authToken;
  }

  // Headers oluşturma
  getHeaders() {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.authToken}`
    };
  }

  // Poker oturumu oluşturma
  async createSession(teamId, storyTitle, storyDescription) {
    const response = await fetch(`${this.baseUrl}/api/poker/sessions`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({
        teamId,
        storyTitle,
        storyDescription
      })
    });

    if (!response.ok) {
      throw new Error('Poker oturumu oluşturulamadı');
    }

    return response.json();
  }

  // Poker oturumuna katılma
  async joinSession(sessionId) {
    const response = await fetch(`${this.baseUrl}/api/poker/sessions/${sessionId}/join`, {
      method: 'POST',
      headers: this.getHeaders()
    });

    if (!response.ok) {
      throw new Error('Poker oturumuna katılınamadı');
    }

    return response.json();
  }

  // Oy kullanma
  async castVote(sessionId, voteValue) {
    const response = await fetch(`${this.baseUrl}/api/poker/votes`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({
        sessionId,
        voteValue
      })
    });

    if (!response.ok) {
      throw new Error('Oy kullanılamadı');
    }

    return response.json();
  }

  // Oyları açma
  async revealVotes(sessionId) {
    const response = await fetch(`${this.baseUrl}/api/poker/sessions/${sessionId}/reveal`, {
      method: 'POST',
      headers: this.getHeaders()
    });

    if (!response.ok) {
      throw new Error('Oylar açılamadı');
    }

    return response.json();
  }

  // Oturumu tamamlama
  async completeSession(sessionId, finalEstimate) {
    const response = await fetch(`${this.baseUrl}/api/poker/sessions/${sessionId}/complete?finalEstimate=${finalEstimate}`, {
      method: 'POST',
      headers: this.getHeaders()
    });

    if (!response.ok) {
      throw new Error('Oturum tamamlanamadı');
    }

    return response.json();
  }

  // Aktif oturum alma
  async getActiveSession(teamId) {
    const response = await fetch(`${this.baseUrl}/api/poker/teams/${teamId}/active-session`, {
      method: 'GET',
      headers: this.getHeaders()
    });

    if (!response.ok) {
      throw new Error('Aktif oturum alınamadı');
    }

    return response.json();
  }

  // Takım oturumları alma
  async getTeamSessions(teamId) {
    const response = await fetch(`${this.baseUrl}/api/poker/teams/${teamId}/sessions`, {
      method: 'GET',
      headers: this.getHeaders()
    });

    if (!response.ok) {
      throw new Error('Takım oturumları alınamadı');
    }

    return response.json();
  }
}
```

## React Component Örneği

```jsx
import React, { useState, useEffect } from 'react';
import PokerWebSocketService from '../services/PokerWebSocketService';
import PokerApiService from '../services/PokerApiService';

const PokerSession = ({ teamId, userId, userName, authToken }) => {
  const [session, setSession] = useState(null);
  const [votes, setVotes] = useState([]);
  const [userVote, setUserVote] = useState(null);
  const [isConnected, setIsConnected] = useState(false);

  const pokerApi = new PokerApiService('http://localhost:8080', authToken);

  const fibonacciNumbers = ['1', '2', '3', '5', '8', '13', '21', '34', '55', '89', '?'];

  useEffect(() => {
    // WebSocket bağlantısını kur
    PokerWebSocketService.connect(authToken);
    
    // Takım poker odasına abone ol
    PokerWebSocketService.subscribeToTeamPoker(teamId, handlePokerMessage);
    
    // Odaya katıl
    PokerWebSocketService.sendJoinMessage(teamId, userId, userName);
    
    // Aktif oturumu getir
    loadActiveSession();

    return () => {
      // Temizlik
      PokerWebSocketService.sendLeaveMessage(teamId, userId, userName);
      PokerWebSocketService.unsubscribeFromTeamPoker(teamId);
      PokerWebSocketService.disconnect();
    };
  }, [teamId]);

  const handlePokerMessage = (message) => {
    console.log('Poker message received:', message);
    
    switch (message.type) {
      case 'SESSION_CREATED':
      case 'SESSION_UPDATED':
        setSession(message.data);
        break;
      case 'VOTE_CAST':
        updateVotes(message.data);
        break;
      case 'VOTES_REVEALED':
        setSession(message.data);
        setVotes(message.data.votes);
        break;
      case 'SESSION_COMPLETED':
        setSession(message.data);
        break;
      case 'USER_JOINED':
        console.log(`${message.userName} poker odasına katıldı`);
        break;
      case 'USER_LEFT':
        console.log(`${message.userName} poker odasından ayrıldı`);
        break;
    }
  };

  const loadActiveSession = async () => {
    try {
      const response = await pokerApi.getActiveSession(teamId);
      if (response.success && response.data) {
        setSession(response.data);
        setVotes(response.data.votes || []);
      }
    } catch (error) {
      console.error('Aktif oturum yüklenemedi:', error);
    }
  };

  const createSession = async (storyTitle, storyDescription) => {
    try {
      const response = await pokerApi.createSession(teamId, storyTitle, storyDescription);
      if (response.success) {
        setSession(response.data);
      }
    } catch (error) {
      console.error('Oturum oluşturulamadı:', error);
    }
  };

  const castVote = async (voteValue) => {
    try {
      const response = await pokerApi.castVote(session.id, voteValue);
      if (response.success) {
        setUserVote(voteValue);
      }
    } catch (error) {
      console.error('Oy kullanılamadı:', error);
    }
  };

  const revealVotes = async () => {
    try {
      const response = await pokerApi.revealVotes(session.id);
      if (response.success) {
        setSession(response.data);
        setVotes(response.data.votes);
      }
    } catch (error) {
      console.error('Oylar açılamadı:', error);
    }
  };

  const updateVotes = (newVote) => {
    setVotes(prevVotes => {
      const existingVoteIndex = prevVotes.findIndex(v => v.user.id === newVote.user.id);
      if (existingVoteIndex >= 0) {
        const updatedVotes = [...prevVotes];
        updatedVotes[existingVoteIndex] = newVote;
        return updatedVotes;
      } else {
        return [...prevVotes, newVote];
      }
    });
  };

  if (!session) {
    return (
      <div className="poker-session">
        <h2>Poker Oturumu Oluştur</h2>
        <CreateSessionForm onSubmit={createSession} />
      </div>
    );
  }

  return (
    <div className="poker-session">
      <div className="session-header">
        <h2>{session.storyTitle}</h2>
        <p>{session.storyDescription}</p>
        <span className={`status ${session.status.toLowerCase()}`}>
          {session.status}
        </span>
      </div>

      <div className="voting-area">
        <h3>Fibonacci Kartları</h3>
        <div className="cards">
          {fibonacciNumbers.map(number => (
            <button
              key={number}
              className={`card ${userVote === number ? 'selected' : ''}`}
              onClick={() => castVote(number)}
              disabled={session.status === 'REVEALED' || session.status === 'COMPLETED'}
            >
              {number}
            </button>
          ))}
        </div>
      </div>

      <div className="votes-area">
        <h3>Oylar ({votes.length})</h3>
        <div className="votes">
          {votes.map(vote => (
            <div key={vote.id} className="vote">
              <span className="user">{vote.user.firstName} {vote.user.lastName}</span>
              <span className="value">
                {vote.isRevealed ? vote.voteValue : '?'}
              </span>
            </div>
          ))}
        </div>
      </div>

      {session.createdBy.id === userId && session.status === 'VOTING' && (
        <button onClick={revealVotes} className="reveal-button">
          Oyları Açık
        </button>
      )}
    </div>
  );
};

export default PokerSession;
```

## CSS Stillleri

```css
.poker-session {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.session-header {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
}

.status.waiting { background: #ffc107; color: #000; }
.status.voting { background: #007bff; color: #fff; }
.status.revealed { background: #28a745; color: #fff; }
.status.completed { background: #6c757d; color: #fff; }

.cards {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 20px 0;
}

.card {
  width: 60px;
  height: 80px;
  border: 2px solid #007bff;
  background: white;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.card:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

.card.selected {
  background: #007bff;
  color: white;
}

.card:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.votes {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.vote {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.reveal-button {
  background: #28a745;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
}

.reveal-button:hover {
  background: #218838;
}
```

## Mesaj Tipleri

WebSocket üzerinden gelen mesaj tipleri:

- `SESSION_CREATED`: Yeni poker oturumu oluşturuldu
- `SESSION_UPDATED`: Poker oturumu güncellendi
- `VOTE_CAST`: Yeni oy kullanıldı
- `VOTES_REVEALED`: Oylar açıldı
- `SESSION_COMPLETED`: Oturum tamamlandı
- `USER_JOINED`: Kullanıcı odaya katıldı
- `USER_LEFT`: Kullanıcı odadan ayrıldı
- `ERROR`: Hata oluştu

## Hata Yönetimi

```javascript
// WebSocket bağlantı hatalarını yönetme
PokerWebSocketService.client.onStompError = (frame) => {
  console.error('STOMP Hatası:', frame);
  // Kullanıcıya hata bildirimi göster
  showErrorNotification('Bağlantı hatası oluştu');
};

// API hatalarını yönetme
const handleApiError = (error) => {
  if (error.response) {
    // Server yanıt verdi ama hata kodu döndü
    console.error('API Hatası:', error.response.data);
    showErrorNotification(error.response.data.message || 'Bir hata oluştu');
  } else if (error.request) {
    // İstek gönderildi ama yanıt alınmadı
    console.error('Ağ Hatası:', error.request);
    showErrorNotification('Sunucuya ulaşılamıyor');
  } else {
    // İstek oluşturulurken hata oluştu
    console.error('Hata:', error.message);
    showErrorNotification('Beklenmedik bir hata oluştu');
  }
};
```

Bu dokümandaki örnekleri kullanarak Scrum Poker sistemini frontend uygulamanıza entegre edebilirsiniz. Sistem gerçek zamanlı iletişim sağlar ve tüm takım üyeleri eş zamanlı olarak poker oturumuna katılabilir.
