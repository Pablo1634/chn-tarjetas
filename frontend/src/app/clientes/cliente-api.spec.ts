import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  ClienteSolicitud,
  ClienteRespuesta
} from './cliente';

@Injectable({
  providedIn: 'root'
})
export class ClienteApiService {
  private readonly urlClientes = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient) {}

  listar(): Observable<ClienteRespuesta[]> {
    return this.http.get<ClienteRespuesta[]>(this.urlClientes);
  }

  crear(cliente: ClienteSolicitud): Observable<ClienteRespuesta> {
    return this.http.post<ClienteRespuesta>(this.urlClientes, cliente);
  }
}