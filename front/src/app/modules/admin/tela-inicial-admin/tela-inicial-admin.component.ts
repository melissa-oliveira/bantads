import { Component } from '@angular/core';
import { GerenteDashboard, ListaGerentesDashboard } from '../../shared';
import { GerenteService } from '../services/gerente.service';

@Component({
  selector: 'app-tela-inicial-admin',
  templateUrl: './tela-inicial-admin.component.html',
  styleUrls: ['./tela-inicial-admin.component.css']
})
export class TelaInicialAdminComponent {
  gerentesDashboard: GerenteDashboard[] = [];

  constructor(private gerenteService: GerenteService) { }

  ngOnInit(): void {
    this.gerentesDashboard = [];
    this.listarTodos();
  }

  listarTodos(): void {
    this.gerenteService.listarTodosDashboard().subscribe({
      next: (data: ListaGerentesDashboard) => {
        if (data == null) {
          this.gerentesDashboard = [];
        } else {
          this.gerentesDashboard = data.gerentesDashboard;
        }
      }
    });
  }
}
