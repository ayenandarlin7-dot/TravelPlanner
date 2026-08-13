document.addEventListener('DOMContentLoaded', () => {
  
  // ----------------------------------------------------
  // 1. TAB SWITCHING LOGIC (Sidebar Navigation)
  // ----------------------------------------------------
  const navButtons = document.querySelectorAll('.nav-btn');
  const tabContents = document.querySelectorAll('.tab-content');
  const pageTitle = document.getElementById('pageTitle');

  const pageTitles = {
    'dashboard': 'Dashboard',
    'plan': 'Plan New Trip',
    'my-trips': 'My Trips',
    'history': 'Trip History',
    'profile': 'User Profile',
    'about': 'About TravelMate AI'
  };

  navButtons.forEach(button => {
    button.addEventListener('click', () => {
      const targetTab = button.getAttribute('data-tab');

      // Update Nav Buttons Active Style
      navButtons.forEach(btn => {
        btn.className = "nav-btn w-full flex items-center gap-3 px-4 py-3 text-slate-400 hover:bg-teal-900/40 hover:text-white rounded-xl transition";
      });
      button.className = "nav-btn w-full flex items-center gap-3 px-4 py-3 bg-teal-900 text-white font-medium rounded-xl transition";

      // Show Selected Tab Content
      tabContents.forEach(content => content.classList.add('hidden'));

      if (targetTab === 'plan' || targetTab === 'dashboard') {
        document.getElementById('tab-dashboard').classList.remove('hidden');
      } else {
        document.getElementById(`tab-${targetTab}`)?.classList.remove('hidden');
      }

      // Dynamic Title Change
      if (pageTitle && pageTitles[targetTab]) {
        pageTitle.innerText = pageTitles[targetTab];
      }
    });
  });

  // ----------------------------------------------------
  // 2. TRIP GENERATOR LOGIC
  // ----------------------------------------------------
  const tripForm = document.getElementById('tripForm');
  if (!tripForm) return;

  const cityData = {
    'Naypyidaw': {
      mapUrl: 'https://maps.google.com/maps?q=Naypyidaw&t=&z=10&ie=UTF8&iwloc=&output=embed',
      hotel: 'Lake Garden Naypyidaw Hotel',
      hotelImg: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=400',
      attractions: ['Uppatasanti Pagoda', 'National Museum Naypyidaw'],
      baseCost: { transport: 72000, hotel: 195000, food: 200000, attractions: 40000 }
    },
    'Bagan': {
      mapUrl: 'https://maps.google.com/maps?q=Bagan&t=&z=10&ie=UTF8&iwloc=&output=embed',
      hotel: 'Bagan Heritage Hotel',
      hotelImg: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=400',
      attractions: ['Ananda Temple', 'Shwesandaw Pagoda View'],
      baseCost: { transport: 90000, hotel: 250000, food: 220000, attractions: 60000 }
    },
    'Mandalay': {
      mapUrl: 'https://maps.google.com/maps?q=Mandalay&t=&z=10&ie=UTF8&iwloc=&output=embed',
      hotel: 'Mandalay Hill Resort',
      hotelImg: 'https://images.unsplash.com/photo-1596422846543-75c6fc197f07?q=80&w=400',
      attractions: ['U Bein Bridge', 'Mandalay Palace'],
      baseCost: { transport: 80000, hotel: 210000, food: 180000, attractions: 50000 }
    },
    'Inle Lake': {
      mapUrl: 'https://maps.google.com/maps?q=Inle+Lake&t=&z=10&ie=UTF8&iwloc=&output=embed',
      hotel: 'Inle Resort & Spa',
      hotelImg: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=400',
      attractions: ['Phaung Daw Oo Pagoda', 'Floating Gardens'],
      baseCost: { transport: 110000, hotel: 280000, food: 230000, attractions: 70000 }
    }
  };

  tripForm.addEventListener('submit', (e) => {
    e.preventDefault();

    document.getElementById('btnText').classList.add('hidden');
    document.getElementById('btnLoader').classList.remove('hidden');

    setTimeout(() => {
      const start = document.getElementById('startCity').value;
      const dest = document.getElementById('destCity').value;
      const budget = parseInt(document.getElementById('userBudget').value) || 0;

      const data = cityData[dest] || cityData['Naypyidaw'];

      document.getElementById('mapFrame').src = data.mapUrl;
      document.getElementById('routeTitle').innerText = `${start} → ${dest}`;

      const totalCost = data.baseCost.transport + data.baseCost.hotel + data.baseCost.food + data.baseCost.attractions;
      const remaining = budget - totalCost;

      document.getElementById('costTransport').innerText = `${data.baseCost.transport.toLocaleString()} MMK`;
      document.getElementById('costHotel').innerText = `${data.baseCost.hotel.toLocaleString()} MMK`;
      document.getElementById('costFood').innerText = `${data.baseCost.food.toLocaleString()} MMK`;
      document.getElementById('costAttractions').innerText = `${data.baseCost.attractions.toLocaleString()} MMK`;
      document.getElementById('totalCost').innerText = `${totalCost.toLocaleString()} MMK`;
      document.getElementById('remainingBudget').innerText = `${remaining.toLocaleString()} MMK`;

      const badge = document.getElementById('budgetBadge');
      if (remaining < 0) {
        badge.className = "bg-red-50 text-red-700 text-xs py-2 rounded-xl text-center font-bold";
        badge.innerHTML = `<i class="fa-solid fa-circle-xmark"></i> Over Budget`;
      } else {
        badge.className = "bg-emerald-50 text-emerald-700 text-xs py-2 rounded-xl text-center font-bold";
        badge.innerHTML = `<i class="fa-solid fa-circle-check"></i> Within Budget`;
      }

      document.getElementById('hotelName').innerText = data.hotel;
      document.getElementById('hotelImg').src = data.hotelImg;

      const attrContainer = document.getElementById('attractionList');
      attrContainer.innerHTML = data.attractions.map(item => `
        <div class="bg-slate-50 p-2 rounded-xl text-xs font-semibold text-slate-700 flex items-center gap-2">
          <i class="fa-solid fa-location-dot text-amber-500"></i> ${item}
        </div>
      `).join('');

      document.getElementById('btnText').classList.remove('hidden');
      document.getElementById('btnLoader').classList.add('hidden');
    }, 500);
  });

  // Save Trip Logic
  document.getElementById('saveTripBtn')?.addEventListener('click', () => {
    const dest = document.getElementById('destCity').value;
    const start = document.getElementById('startCity').value;
    const totalCost = document.getElementById('totalCost').innerText;
    const container = document.getElementById('savedTripsContainer');

    const cardHTML = `
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-3">
        <div class="flex justify-between items-center">
          <span class="text-xs font-bold text-teal-800 bg-teal-50 px-2.5 py-1 rounded-lg">${start} → ${dest}</span>
          <span class="text-xs text-slate-400">Saved</span>
        </div>
        <div class="text-lg font-bold text-slate-800">${totalCost}</div>
        <button class="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition">
          View Details
        </button>
      </div>
    `;

    container.innerHTML += cardHTML;
    alert('Trip successfully saved to "My Trips" tab!');
  });

});