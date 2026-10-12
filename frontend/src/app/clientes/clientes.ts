import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { ClienteApiService } from './cliente-api';
import { ClienteRespuesta } from './cliente';

@Component({
  selector: 'app-clientes',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './clientes.html',
  styleUrl: './clientes.scss'
})
export class Clientes implements OnInit {
  clientes: ClienteRespuesta[] = [];
  cargando = true;
  mensajeError = '';

  constructor(private clienteApiService: ClienteApiService) {}

  ngOnInit(): void {
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.clienteApiService.listar().subscribe({
      next: (clientes) => {
        console.log('Respuesta del backend:', clientes);
        console.log('¿Es un arreglo?', Array.isArray(clientes));
        console.log('Cantidad recibida:', clientes?.length);

    this.clientes = clientes;
    this.cargando = false;
        },
      error: () => {
        this.mensajeError = 'No fue posible cargar los clientes.';
        this.cargando = false;
      }
    });
  }
}