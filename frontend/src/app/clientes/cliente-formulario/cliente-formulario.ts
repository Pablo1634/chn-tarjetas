import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { ClienteApiService } from '../cliente-api';

@Component({
  selector: 'app-cliente-formulario',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './cliente-formulario.html',
  styleUrl: './cliente-formulario.scss'
})
export class ClienteFormulario {
  formularioCliente = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.maxLength(100)
    ]),
    apellido: new FormControl('', [
      Validators.required,
      Validators.maxLength(100)
    ]),
    numeroIdentificacion: new FormControl('', [
      Validators.required,
      Validators.maxLength(30)
    ]),
    fechaNacimiento: new FormControl('', [
      Validators.required,
      (control) => {
        if (!control.value) {
          return null;
        }

        const fechaNacimiento = new Date(control.value + 'T00:00:00');
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        if (fechaNacimiento >= hoy) {
          return { fechaNacimientoInvalida: true };
        }

        return null;
      }
    ]),
    direccion: new FormControl('', [
      Validators.required,
      Validators.maxLength(250)
    ]),
    correoElectronico: new FormControl('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(150)
    ]),
    telefono: new FormControl('', [
      Validators.required,
      Validators.maxLength(20)
    ])
  });

  guardando = false;
  mensajeError = '';

  constructor(
    private clienteApiService: ClienteApiService,
    private router: Router
  ) {}

  guardar(): void {
    this.mensajeError = '';

    if (this.formularioCliente.invalid) {
      this.formularioCliente.markAllAsTouched();
      return;
    }

    this.guardando = true;

    this.clienteApiService.crear({
      nombre: this.formularioCliente.controls.nombre.value!,
      apellido: this.formularioCliente.controls.apellido.value!,
      numeroIdentificacion:
        this.formularioCliente.controls.numeroIdentificacion.value!,
      fechaNacimiento:
        this.formularioCliente.controls.fechaNacimiento.value!,
      direccion: this.formularioCliente.controls.direccion.value!,
      correoElectronico:
        this.formularioCliente.controls.correoElectronico.value!,
      telefono: this.formularioCliente.controls.telefono.value!
    }).subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/clientes']);
      },
      error: (error: { status?: number }) => {
        this.guardando = false;

        if (error.status === 409) {
          this.mensajeError =
            'Ya existe un cliente con ese número de identificación.';
        } else {
          this.mensajeError =
            'No fue posible guardar el cliente. Verifica el servidor e inténtalo de nuevo.';
        }
      }
    });
  }
}