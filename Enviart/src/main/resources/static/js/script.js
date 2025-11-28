document.addEventListener('DOMContentLoaded', () => {
  const carousel = document.getElementById('carousel');
  if (!carousel) return; // Sale si no existe el carrusel

  const slides = Array.from(carousel.children);
  const total = slides.length;
  let index = 0;
  let autoSlideInterval;

  // Ajustar ancho del carrusel y de los slides
  carousel.style.display = 'flex';
  carousel.style.width = `${total * 100}%`;
  slides.forEach(slide => {
    slide.style.minWidth = `${100 / total}%`;
    slide.style.height = '100%';
  });

  function showSlide(i) {
    const offset = -i * (100 / total);
    carousel.style.transform = `translateX(${offset}%)`;
    carousel.style.transition = 'transform 0.5s ease-in-out';
  }

  function startAutoSlide() {
    autoSlideInterval = setInterval(() => {
      index = (index + 1) % total;
      showSlide(index);
    }, 6000);
  }

  function resetAutoSlide() {
    clearInterval(autoSlideInterval);
    startAutoSlide();
  }

  // Eventos de botones
  const nextBtn = document.getElementById('next');
  const prevBtn = document.getElementById('prev');

  if (nextBtn) {
    nextBtn.addEventListener('click', () => {
      index = (index + 1) % total;
      showSlide(index);
      resetAutoSlide(); // Reinicia el temporizador al interactuar
    });
  }

  if (prevBtn) {
    prevBtn.addEventListener('click', () => {
      index = (index - 1 + total) % total;
      showSlide(index);
      resetAutoSlide(); // Reinicia el temporizador al interactuar
    });
  }

  // Iniciar
  showSlide(index);
  startAutoSlide();
});