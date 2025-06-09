const PDFDocument = require('pdfkit');

/**
 * Crea un PDF de confirmación de reservación con los QR.
 * @param {Object} data - Datos de la reservación.
 * @param {string} data.name - Nombre del titular.
 * @param {string} data.experienceTitle - Título de la experiencia.
 * @param {string} data.date - Fecha del evento.
 * @param {number} data.people - Cantidad de personas.
 * @param {string[]} data.qrCodes - Array de rutas de imagen o buffers/base64 de QR.
 * @returns {Promise<Buffer>} - Resolución con el buffer del PDF generado.
 */
async function createBookingPDF({ name, experienceTitle, date, people, qrCodes }) {
  return new Promise((resolve, reject) => {
    const doc = new PDFDocument({ margin: 40 });
    const buffers = [];
    doc.on('data', buffers.push.bind(buffers));
    doc.on('end', () => {
      const pdfBuffer = Buffer.concat(buffers);
      resolve(pdfBuffer);
    });

    // Logo (opcional)
    // doc.image(path.join(__dirname, 'logo.png'), doc.page.width - 120, 20, { width: 80 });

    // Título
    doc
      .fontSize(22)
      .fillColor('#0a7ea4')
      .text('Confirmación de Reservación', { align: 'center' })
      .moveDown(0.5);

    // Línea decorativa
    doc
      .moveTo(40, doc.y)
      .lineTo(doc.page.width - 40, doc.y)
      .strokeColor('#0a7ea4')
      .lineWidth(2)
      .stroke()
      .moveDown();

    // Datos principales
    doc
      .fontSize(13)
      .fillColor('#222')
      .text(`Nombre: `, { continued: true })
      .font('Helvetica-Bold').text(name)
      .font('Helvetica').text(`Experiencia: `, { continued: true })
      .font('Helvetica-Bold').text(experienceTitle)
      .font('Helvetica').text(`Fecha: `, { continued: true })
      .font('Helvetica-Bold').text(date)
      .font('Helvetica').text(`Cantidad de personas: `, { continued: true })
      .font('Helvetica-Bold').text(people.toString())
      .moveDown();

    // Instrucciones
    doc
      .fontSize(12)
      .fillColor('#0a7ea4')
      .text('Presenta el siguiente código QR para cada entrada al ingresar al evento.', { align: 'center' })
      .moveDown();

    // QRs
    qrCodes.forEach((qr, idx) => {
      doc
        .addPage() // Cada QR en una página nueva para evitar solapamiento
        .fontSize(16)
        .fillColor('#0a7ea4')
        .text(`Entrada #${idx + 1}`, { align: 'center' })
        .moveDown(1);

      // Centrar el QR verticalmente
      const qrY = doc.page.height / 2 - 60;
      if (typeof qr === 'string' && qr.startsWith('data:image')) {
        const base64Data = qr.split(',')[1];
        const imgBuffer = Buffer.from(base64Data, 'base64');
        doc.image(imgBuffer, doc.page.width / 2 - 60, qrY, { width: 120 });
      } else {
        doc.image(qr, doc.page.width / 2 - 60, qrY, { width: 120 });
      }

      doc.moveDown(4);
    });

    // Footer
    doc
      .fontSize(10)
      .fillColor('#888')
      .text('GourmetGo © ' + new Date().getFullYear(), 0, doc.page.height - 50, { align: 'center' });

    doc.end();
  });
}

module.exports = { createBookingPDF };