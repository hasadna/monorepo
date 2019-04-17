import numpy as np
import math


def factorial(n):
    return int(math.gamma(n + 1))


def n_choose_k(n, k):
    return factorial(n) / (factorial(k) * factorial(n - k))


def generate_synthetic_a(num_of_professions):
    num_of_rows = int(n_choose_k(num_of_professions, 2))
    A = np.zeros(shape=(num_of_rows, num_of_professions), dtype=np.float32)
    row = 0

    for i in range(num_of_professions):
        for j in range(i + 1, num_of_professions):
            A[row][i] = 1
            A[row][j] = -1
            row += 1

    return A


def generate_synthetic_c(m, i, a):
    c = np.dot(a, m[i].transpose())
    return c


def solve(A, c):
    row_in_m = np.linalg.lstsq(A, c, None)[0]
    return row_in_m

def build_m(num_of_professions, c_matrix, num_of_characteristics):
    A = generate_synthetic_a(num_of_professions)
    m = np.zeros((num_of_characteristics, num_of_professions), dtype=np.float32)
    for i in range(num_of_characteristics):
        c = c_matrix[i]
        row_in_m = solve(A, c)
        row_in_m = (row_in_m - np.min(row_in_m)) / np.ptp(row_in_m)
        m[i] = row_in_m
    return m



def run_check (m):
    num_of_professions = m.shape[1]
    A = generate_synthetic_a(num_of_professions)

    num_of_rows_in_m = m.shape[0]
    new_m = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
    c_matrix = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
    for i in range(num_of_rows_in_m):
        c = generate_synthetic_c(m, i, A)
        c_matrix[i] = c
        row_in_m = solve(A, c)
        new_m[i] = row_in_m
    Ax = np.dot(A, new_m.transpose())
    errors = np.subtract(Ax, c_matrix.transpose())

    return new_m, errors

#
# def find_c_and_a(m):
#     # A function that receives a matrix m with size k*n, such that k is a characteristics of
#     # professions and n is the number of professions that are compared.
#     # The function founds using the List Square algorithm, the vector c with size k*1, and a
#     # matrix A, to solve the equation Ax = c.
#     # The output is a matrix with size k*n which is estimated, the matrix A with size
#     # (num_of_professions Choose 2) * (professions) and the error matrix with size k*n
#     # (which is calculated by Ax - c = error)
#     num_of_professions = m.shape[1]
#     num_of_rows = int(n_choose_k(num_of_professions, 2))
#     A = np.zeros(shape=(num_of_rows, num_of_professions), dtype=np.float32)
#     row = 0
#
#     for i in range(num_of_professions):
#         for j in range(i+1, num_of_professions):
#             A[row][i] = 1
#             A[row][j] = -1
#             row += 1
#
#     num_of_rows_in_m = np.size(m, 0)
#     c_matrix = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
#     for i in range(m.shape[0]):
#         c = np.dot(A, m[i].transpose())
#         c_matrix[i] = c
#
#     new_m = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
#     for i in range(c_matrix.shape[0]):
#         row_in_m = np.linalg.lstsq(A, c_matrix[i], None)[0]
#         new_m[i] = row_in_m
#
#     Ax = np.dot(A, new_m.transpose())
#     errors = np.subtract(Ax, c_matrix.transpose())
#
#     return new_m, A, errors

# m = np.array([[-2, -2, 4], [-1, -1, 2]])
# new_m, errors = run_check(m)
# print("1 ")
# print(new_m)
# print("2 ")
# print(errors)
# M_matrix, A_matrix, E_matrix = find_c_and_a(m)
# print("The A matrix: ")
# print(A_matrix)
# print("The M matrix: ")
# print(M_matrix)
# print("The Error matrix: ")
# print(E_matrix)

# The professions are - driver, lawyer, programmer
# The characteristics are - social status, working with people, analytical thinking
c_matrix = [[-3, -3, -2],
            [-1, 2, 3],
            [-3, -3, -2]]
m = build_m(3, c_matrix, 3)
print(m)