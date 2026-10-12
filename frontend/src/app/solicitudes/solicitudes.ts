import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

interface Solicitud {
  id: number;
  clienteId: number;
  tipoTarjeta: string;
  ingresoMensualDeclarado: number;
  fechaSolicitud: string;
  estado: string;
  fechaDecision?: string | null;
  comentarioDecision?: string | null;
  montoAprobado?: number | null;
}

@Component({
  selector: 'app-solicitudes',
  imports: [
    MatCardModule,
    MatButtonModule,
    CurrencyPipe,
    DatePipe
  ],
  templateUrl: './solicitudes.html',
  styleUrl: './solicitudes.scss'
})
export class Solicitudes implements OnInit {
  solicitudes: Solicitud[] = [];
  cargando = true;
  mensaje = '';

  private readonly url = 'http://localhost:8080/api/solicitudes';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.cargando = true;
    this.mensaje = '';

    this.http.get<Solicitud[]>(this.url).subscribe({
      next: (respuesta) => {
        console.log('Solicitudes recibidas:', respuesta);
        this.solicitudes = respuesta;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar solicitudes:', error);
        this.mensaje = 'No fue posible cargar las solicitudes.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  decidir(
    id: number,
    decision: 'aprobar' | 'rechazar'
  ): void {
    const comentario = window.prompt(
      'Ingresa el motivo o comentario de la decisión:'
    );

    if (comentario === null || !comentario.trim()) {
      return;
    }

    let cuerpo: {
      comentarioDecision: string;
      montoAprobado?: number;
    } = {
      comentarioDecision: comentario.trim()
    };

    if (decision === 'aprobar') {
      const entrada = window.prompt(
        'Ingresa el monto de crédito aprobado:'
      );

      if (entrada === null) {
        return;
      }

      const montoAprobado = Number(entrada);

      if (!Number.isFinite(montoAprobado) || montoAprobado <= 0) {
        window.alert('Ingresa un monto aprobado válido.');
        return;
      }

      cuerpo = {
        comentarioDecision: comentario.trim(),
        montoAprobado
      };
    }

    this.http.put(
      `${this.url}/${id}/${decision}`,
      cuerpo
    ).subscribe({
      next: () => {
        this.mensaje = 'La decisión se registró correctamente.';
        this.cargarSolicitudes();
      },
      error: (error) => {
        console.error('Error al registrar la decisión:', error);
        this.mensaje =
          'No se pudo registrar la decisión. Verifica el formato requerido por el backend.';
      }
    });
  }
}