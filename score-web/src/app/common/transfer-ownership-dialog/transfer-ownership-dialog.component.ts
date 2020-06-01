import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialogRef, MatPaginator, MatSort, MatTableDataSource, PageEvent} from '@angular/material';
import {AccountListService} from '../../account-management/domain/account-list.service';
import {AccountList, AccountListRequest} from '../../account-management/domain/accounts';
import {PageRequest} from '../../basis/basis';
import {SelectionModel} from '@angular/cdk/collections';
import {AuthService} from '../../authentication/auth.service';

@Component({
  selector: 'score-transfer-ownership-dialog',
  templateUrl: './transfer-ownership-dialog.component.html',
  styleUrls: ['./transfer-ownership-dialog.component.css']
})
export class TransferOwnershipDialogComponent implements OnInit {

  displayedColumns: string[] = [
    'select', 'loginId', 'name', 'organization', 'developer'
  ];
  dataSource = new MatTableDataSource<AccountList>();
  selection = new SelectionModel<AccountList>(false, []);

  loginIdList: string[] = [];
  request: AccountListRequest;

  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  constructor(
    public dialogRef: MatDialogRef<TransferOwnershipDialogComponent>,
    private accountService: AccountListService,
    private authService: AuthService) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.request = new AccountListRequest();

    this.paginator.pageIndex = 0;
    this.paginator.pageSize = 10;
    this.paginator.length = 0;

    this.sort.active = 'name';
    this.sort.direction = 'asc';
    this.sort.sortChange.subscribe(() => {
      this.paginator.pageIndex = 0;
      this.onChange();
    });

    this.onChange();
  }

  onPageChange(event: PageEvent) {
    this.loadAccounts();
  }

  onChange() {
    this.paginator.pageIndex = 0;
    this.loadAccounts();
  }

  loadAccounts() {
    this.request.page = new PageRequest(
      this.sort.active, this.sort.direction,
      this.paginator.pageIndex, this.paginator.pageSize);

    this.accountService.getAccountsList(this.request, true).subscribe(resp => {
      this.paginator.length = resp.length;
      this.dataSource.data = resp.list;
    });
  }

  select(row: AccountList) {
    this.selection.select(row);
  }

  toggle(row: AccountList) {
    if (this.isSelected(row)) {
      this.selection.deselect(row);
    } else {
      this.select(row);
    }
  }

  isSelected(row: AccountList) {
    return this.selection.isSelected(row);
  }

}
