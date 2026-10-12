
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

interface Tarjeta {
  id: number;
  numeroTarjeta: string;
  clienteId: number;
  solicitudId: number;
  tipoTarjeta: string;
  limiteCredito: number;
  saldoActual: number;
  fechaEmision: string;
}

interface Movimiento {
  id: number;
  tarjetaId: number;
  tipoMovimiento: string;
  monto: number;
  fechaMovimiento: string;
  descripcion: string;
  creadoEn: string;
}

@Component({
  selector: 'app-tarjetas',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    CurrencyPipe,
    DatePipe,
    FormsModule
  ],
  templateUrl: './tarjetas.html',
  styleUrl: './tarjetas.scss'
})
export class Tarjetas implements OnInit {
  tarjetas: Tarjeta[] = [];
  movimientos: Movimiento[] = [];
  tarjetaSeleccionada: number | null = null;

  cargando = true;
  cargandoMovimientos = false;
  guardandoOperacion = false;

  mensaje = '';
  mensajeMovimientos = '';
  mensajeOperacion = '';

  montoOperacion: number | null = null;
  descripcionOperacion = '';
  tipoOperacion: 'CONSUMO' | 'PAGO' = 'CONSUMO';

  private readonly url = 'http://localhost:8080/api/tarjetas';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarTarjetas();
  }

  cargarTarjetas(): void {
    this.cargando = true;
    this.mensaje = '';

    this.http.get<Tarjeta[]>(this.url).subscribe({
      next: (respuesta) => {
        this.tarjetas = respuesta;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar tarjetas:', error);
        this.mensaje = 'No fue posible cargar las tarjetas.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  verMovimientos(tarjetaId: number): void {
    this.tarjetaSeleccionada = tarjetaId;
    this.movimientos = [];
    this.cargandoMovimientos = true;
    this.mensajeMovimientos = '';
    this.mensajeOperacion = '';

    this.http
      .get<Movimiento[]>(`${this.url}/${tarjetaId}/movimientos`)
      .subscribe({
        next: (respuesta) => {
          this.movimientos = respuesta;
          this.cargandoMovimientos = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error al cargar movimientos:', error);
          this.mensajeMovimientos =
            'No fue posible cargar los movimientos de esta tarjeta.';
          this.cargandoMovimientos = false;
          this.cdr.detectChanges();
        },
        complete: () => {
          this.cargandoMovimientos = false;
          this.cdr.detectChanges();
        }
      });
  }

  registrarOperacion(): void {
    if (this.tarjetaSeleccionada === null) {
      this.mensajeOperacion = 'Selecciona una tarjeta.';
      return;
    }

    if (
      this.montoOperacion === null ||
      !Number.isFinite(this.montoOperacion) ||
      this.montoOperacion <= 0
    ) {
      this.mensajeOperacion = 'Ingresa un monto mayor que cero.';
      return;
    }

    if (!this.descripcionOperacion.trim()) {
      this.mensajeOperacion = 'Ingresa una descripción.';
      return;
    }

    const tarjetaId = this.tarjetaSeleccionada;
    const tipo = this.tipoOperacion;
    const accion = tipo === 'CONSUMO' ? 'consumos' : 'pagos';
    const urlOperacion = `${this.url}/${tarjetaId}/${accion}`;

    const cuerpo = {
      monto: this.montoOperacion,
      descripcion: this.descripcionOperacion.trim()
    };

    this.guardandoOperacion = true;
    this.mensajeOperacion = '';

    this.http.post(urlOperacion, cuerpo).subscribe({
      next: () => {
        this.guardandoOperacion = false;
        this.montoOperacion = null;
        this.descripcionOperacion = '';
        this.mensajeOperacion = 'Operación registrada correctamente.';

        this.cargarTarjetas();
        this.verMovimientos(tarjetaId);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al registrar operación:', error);
        this.guardandoOperacion = false;
        this.mensajeOperacion =
          error.error?.detail ||
          'No fue posible registrar la operación.';
        this.cdr.detectChanges();
      },
      complete: () => {
        this.guardandoOperacion = false;
        this.cdr.detectChanges();
      }
    });
  }

  cerrarMovimientos(): void {
    this.tarjetaSeleccionada = null;
    this.movimientos = [];
    this.mensajeMovimientos = '';
    this.mensajeOperacion = '';
    this.montoOperacion = null;
    this.descripcionOperacion = '';
    this.tipoOperacion = 'CONSUMO';
  }
}
