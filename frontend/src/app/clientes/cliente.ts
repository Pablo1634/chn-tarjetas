export interface ClienteSolicitud {
  nombre: string;
  apellido: string;
  numeroIdentificacion: string;
  fechaNacimiento: string;
  direccion: string;
  correoElectronico: string;
  telefono: string;
}
export interface ClienteRespuesta {
  id: number;
  nombre: string;
  apellido: string;
  numeroIdentificacion: string;
  fechaNacimiento: string;
  direccion: string;
  correoElectronico: string;
  telefono: string;
}