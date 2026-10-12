import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'clientes',
    pathMatch: 'full'
  },
  {
    path: 'clientes',
    loadComponent: () =>
      import('./clientes/clientes').then(
        componente => componente.Clientes
      )
  },
  {
    path: 'clientes/nuevo',
    loadComponent: () =>
      import('./clientes/cliente-formulario/cliente-formulario').then(
        componente => componente.ClienteFormulario
      )
  },
  {
    path: 'solicitudes',
    loadComponent: () =>
      import('./solicitudes/solicitudes').then(
        componente => componente.Solicitudes
      )
  },
  {
    path: 'solicitudes/nueva',
    loadComponent: () =>
    import('./solicitudes/solicitud-formulario/solicitud-formulario')
      .then(componente => componente.SolicitudFormulario)
    },
    {
  path: 'tarjetas',
  loadComponent: () =>
    import('./tarjetas/tarjetas').then(componente => componente.Tarjetas)
    }
];