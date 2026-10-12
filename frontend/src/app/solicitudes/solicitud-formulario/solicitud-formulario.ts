import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-solicitud-formulario',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './solicitud-formulario.html',
  styleUrl: './solicitud-formulario.scss'
})
export class SolicitudFormulario {
  clienteId: number | null = null;
  tipoTarjeta = 'CLASICA';
  ingresoMensualDeclarado: number | null = null;
  guardando = false;
  mensaje = '';

  private readonly url = 'http://localhost:8080/api/solicitudes';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  guardar(): void {
    if (
      this.clienteId === null ||
      this.clienteId <= 0 ||
      this.ingresoMensualDeclarado === null ||
      this.ingresoMensualDeclarado <= 0
    ) {
      this.mensaje = 'Ingresa un cliente y un ingreso mensual válido.';
      return;
    }

    const solicitud = {
      clienteId: this.clienteId,
      tipoTarjeta: this.tipoTarjeta,
      ingresoMensualDeclarado: this.ingresoMensualDeclarado
    };

    this.guardando = true;
    this.mensaje = '';

    this.http.post(this.url, solicitud).subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/solicitudes']);
      },
      error: (error) => {
        console.error('Error al crear solicitud:', error);
        this.guardando = false;
        this.mensaje =
          'No fue posible registrar la solicitud. Verifica que el cliente exista.';
      }
    });
  }
}